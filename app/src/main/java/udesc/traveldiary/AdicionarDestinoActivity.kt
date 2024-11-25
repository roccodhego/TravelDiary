package udesc.traveldiary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import udesc.traveldiary.databinding.ActivityAdicionarDestinoBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
                    0 -> verificarPermissoesECapturar()
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

    private fun verificarPermissoesECapturar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_IMAGE_CAPTURE)
        } else {
            abrirCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamera()
        } else {
            Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Erro ao criar arquivo de imagem: ${ex.localizedMessage}", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    fotoPath?.let {
                        binding.imageViewFoto.setImageURI(Uri.parse(it))
                    }
                }
                REQUEST_PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        fotoPath = salvarImagemLocal(selectedImageUri)
                        binding.imageViewFoto.setImageURI(Uri.parse(fotoPath))
                    }
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw IOException("Não foi possível acessar o diretório de armazenamento.")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            fotoPath = absolutePath
        }
    }

    private fun salvarImagemLocal(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "viagem_${System.currentTimeMillis()}.jpg")
            val outputStream = file.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.e("AdicionarDestino", "Erro ao salvar imagem local: $e")
            null
        }
    }
}
