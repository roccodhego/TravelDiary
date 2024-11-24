package udesc.traveldiary

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import udesc.traveldiary.databinding.ActivityListaViagensBinding

class ListaViagensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaViagensBinding
    private lateinit var adapter: ArrayAdapter<Viagens>
    private var listaDeViagens: List<Viagens> = emptyList() // Inicializar com uma lista vazia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaViagensBinding.inflate(layoutInflater)
        setContentView(binding.main)

        // Aciona a Activity de Adicionar um novo destino
        binding.buttonAdicionarViagem.setOnClickListener {
            val intent = Intent(this, AdicionarDestinoActivity::class.java)
            startActivity(intent)
        }

        // Inicializa a lista de viagens
        carregarListaDeViagens()


        // Configura o clique em um item da lista
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val viagemSelecionada = listaDeViagens[position]
            abrirDetalhesViagem(viagemSelecionada)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega a lista de viagens quando a Activity volta para o estado ativo
        carregarListaDeViagens()
    }

    private fun carregarListaDeViagens() {
        val db = DBHelper(this)
        listaDeViagens = db.viagensSelectAll()

        if (listaDeViagens.isNotEmpty()) {
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDeViagens)
            binding.listView.adapter = adapter
        } else {
            Toast.makeText(this, "Nenhuma viagem encontrada.", Toast.LENGTH_SHORT).show()
        }

        for (viagem in listaDeViagens) {
            android.util.Log.d("ListaViagens", "Viagem: ${viagem.nome}, Foto: ${viagem.foto}")
        }

    }

    private fun abrirDetalhesViagem(viagem: Viagens) {
        val intent = Intent(this, DetalhesViagemActivity::class.java)
        intent.putExtra("viagem_id", viagem.id)
        intent.putExtra("viagem_nome", viagem.nome)
        intent.putExtra("viagem_data", viagem.data)
        intent.putExtra("viagem_nota", viagem.nota)
        intent.putExtra("viagem_foto", viagem.foto)
        android.util.Log.d("ListaViagens", "Abrindo detalhes para FotoPath: ${viagem.foto}")

        startActivity(intent)
    }

}
