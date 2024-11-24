package udesc.traveldiary

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import udesc.traveldiary.databinding.ActivityAdicionarDestinoBinding
import java.io.File

class AdicionarDestinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarDestinoBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private var fotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarDestinoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)

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

        binding.buttonSalvarDestino.setOnClickListener {
            val nomeDestino = binding.inputNomeDestino.text.toString().trim()
            val data = binding.inputDataDestino.text.toString().trim()
            val nota = binding.inputNotasDestino.text.toString().trim()

            val addBanco = db.viagemInsert(nomeDestino, data, nota, fotoPath)
            if (addBanco > 0) {
                Toast.makeText(this, "$nomeDestino adicionado!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Falha na inserção!", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonVoltar.setOnClickListener {
            finish()
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
                REQUEST_PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        fotoPath = salvarImagemLocal(selectedImageUri) // Salva localmente
                        binding.imageViewFoto.setImageURI(android.net.Uri.parse(fotoPath))
                    }
                }
            }
        }
    }



    private fun salvarImagemLocal(uri: android.net.Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "viagem_${System.currentTimeMillis()}.jpg")
            val outputStream = file.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath // Retorna o caminho absoluto do arquivo salvo
        } catch (e: Exception) {
            android.util.Log.e("AdicionarDestino", "Erro ao salvar imagem local: $e")
            null
        }
    }



}