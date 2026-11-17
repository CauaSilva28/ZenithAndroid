package com.example.zenithapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CriarTreino : AppCompatActivity() {

    private var idAtleta: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_treino)

        // Recebe o ID do atleta passado pela tela anterior (PerfilAtleta)
        idAtleta = intent.getStringExtra("ATLETA_ID") ?: ""

        if (idAtleta.isEmpty()) {
            Toast.makeText(this, "Erro: Atleta não identificado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mapear elementos da interface
        val btnVoltar = findViewById<Button>(R.id.btnVoltarTreino)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarTreino)

        val etTitulo = findViewById<EditText>(R.id.etTituloTreino)
        val etData = findViewById<EditText>(R.id.etDataTreino)
        val etExercicios = findViewById<EditText>(R.id.etExercicios)
        val etObservacoes = findViewById<EditText>(R.id.etObservacoesTreino)

        // Ação do botão voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // Ação de Salvar o Treino
        btnSalvar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val data = etData.text.toString().trim()
            val exerciciosTexto = etExercicios.text.toString().trim()
            val observacoes = etObservacoes.text.toString().trim()

            // Validação de campos obrigatórios
            if (titulo.isEmpty() || data.isEmpty() || exerciciosTexto.isEmpty()) {
                Toast.makeText(this, "Preencha o título, a data e os exercícios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Converte o texto com quebras de linha em uma lista de strings limpa
            val listaDeExercicios = exerciciosTexto.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (listaDeExercicios.isEmpty()) {
                Toast.makeText(this, "Adicione pelo menos um exercício válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar objeto e salvar no SQLite
            val db = DatabaseHelper(this)
            val novoTreino = Treino(
                idAtleta = idAtleta,
                titulo = titulo,
                data = data,
                exercicios = listaDeExercicios,
                concluido = false,
                observacoes = observacoes
            )

            // Valida se a transação do banco inseriu o treino e os exercícios com sucesso
            val salvouComSucesso = db.inserirTreino(novoTreino)

            if (salvouComSucesso) {
                Toast.makeText(this, "Treino criado com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela e regressa ao Perfil do Atleta
            } else {
                Toast.makeText(this, "Erro ao salvar o treino no banco de dados.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}