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

        // Recebe o ID do atleta passado pelo ecrã anterior (PerfilAtleta)
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

            // Validação
            if (titulo.isEmpty() || data.isEmpty() || exerciciosTexto.isEmpty()) {
                Toast.makeText(this, "Preencha o título, a data e os exercícios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val listaDeExercicios = exerciciosTexto.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (listaDeExercicios.isEmpty()) {
                Toast.makeText(this, "Adicione pelo menos um exercício válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar o objeto Treino (baseado na estrutura do nosso DadosGlobais.kt)
            val novoTreino = Treino(
                idAtleta = idAtleta,
                titulo = titulo,
                data = data,
                exercicios = listaDeExercicios,
                concluido = false, // O atleta ainda não fez o treino
                observacoes = observacoes
            )

            // Guardar no banco de dados temporário
            DadosGlobais.listaTreinos.add(novoTreino)

            Toast.makeText(this, "Treino criado com sucesso!", Toast.LENGTH_SHORT).show()
            finish() // Fecha o ecrã e regressa ao Perfil do Atleta
        }
    }
}