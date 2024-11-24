package udesc.traveldiary


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import udesc.traveldiary.databinding.ActivityDetalhesViagemBinding
import java.io.File

class DetalhesViagemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesViagemBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private var fotoPath: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesViagemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarPermissaoDeLeitura()


        // Recebe os dados da Intent
        val viagemId = intent.getIntExtra("viagem_id", -1)
        val viagemNome = intent.getStringExtra("viagem_nome")
        val viagemData = intent.getStringExtra("viagem_data")
        val viagemNota = intent.getStringExtra("viagem_nota")
        this.fotoPath = intent.getStringExtra("viagem_foto")
        android.util.Log.d("DetalhesViagem", "Foto recebida: $fotoPath")
        carregarImagem(fotoPath)



        // Exibi os dados na interface
        binding.textViewViagemId.text = "ID: $viagemId"
        binding.textViewViagemNome.setText("$viagemNome")
        binding.textViewViagemData.setText("$viagemData")
        binding.textViewViagemNotas.setText("$viagemNota")


        binding.buttonAtualizar.setOnClickListener {
            val db = DBHelper(this)
            val viagemId = intent.getIntExtra("viagem_id", -1)

            if (viagemId != -1) {
                val novaNota = binding.textViewViagemNotas.text.toString().trim()
                val novaData = binding.textViewViagemData.text.toString().trim()
                val novoNome = binding.textViewViagemNome.text.toString().trim()

                val resultado = db.viagemUpdate(viagemId, novoNome, novaData, novaNota, fotoPath)

                if (resultado > 0) {
                    Toast.makeText(this, "Viagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar viagem.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "ID inválido para atualização.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonDeletar.setOnClickListener {
            val db = DBHelper(this)
            val viagemId = intent.getIntExtra("viagem_id", -1)

            if (viagemId != -1) {
                val resultado = db.viagemDelete(viagemId)
                if (resultado > 0) {
                    Toast.makeText(this, "Viagem deletada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Volta para a tela anterior
                } else {
                    Toast.makeText(this, "Erro ao deletar viagem.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "ID inválido para exclusão.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonAdicionarFoto.setOnClickListener {
            val options = arrayOf("Tirar Foto", "Escolher da Galeria")
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Adicionar Foto")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> abrirCamera()
                    1 -> abrirGaleria()
                }
            }
            builder.show()
        }


        binding.buttonVoltar.setOnClickListener {
            finish()
        }


    }

    private fun verificarPermissaoDeLeitura() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão negada. Imagem não pode ser carregada.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.imageViewFoto.setImageBitmap(imageBitmap)
                    fotoPath = salvarFotoLocal(imageBitmap) // Atualiza o caminho da imagem
                }
                REQUEST_PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageViewFoto.setImageBitmap(bitmap)
                        fotoPath = salvarFotoLocal(bitmap) // Atualiza o caminho da imagem
                    }
                }
            }
        }
    }


    private fun salvarFotoLocal(bitmap: Bitmap): String {
        val filename = "viagem_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)
        val fos = file.outputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return file.absolutePath // Retorna o caminho absoluto do arquivo
    }

    private fun carregarImagem(fotoPath: String?) {
        if (fotoPath.isNullOrEmpty()) {
            binding.imageViewFoto.setImageResource(R.drawable.ic_placeholder) // Substitua por uma imagem padrão
            return
        }

        try {
            if (fotoPath.startsWith("content://")) {
                // Caso seja um URI, use ContentResolver
                val uri = android.net.Uri.parse(fotoPath)
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imageViewFoto.setImageBitmap(bitmap)
                inputStream?.close()
            } else {
                // Caso seja um caminho local, carregue diretamente
                val file = File(fotoPath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.imageViewFoto.setImageBitmap(bitmap)
                } else {
                    android.util.Log.e("DetalhesViagem", "Arquivo local não encontrado: $fotoPath")
                    binding.imageViewFoto.setImageResource(R.drawable.ic_placeholder)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetalhesViagem", "Erro ao carregar a imagem: $e")
            binding.imageViewFoto.setImageResource(R.drawable.ic_placeholder)
        }
    }




}