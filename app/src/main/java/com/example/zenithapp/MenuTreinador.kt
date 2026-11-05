package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuTreinador : AppCompatActivity() {

    private lateinit var containerAtletas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_treinador)

        val btnSairTreinador = findViewById<Button>(R.id.btnSairTreinador)
        val btnAddAtleta = findViewById<Button>(R.id.btnAddAtleta)
        containerAtletas = findViewById(R.id.containerAtletas)

        // Botão Adicionar Atleta
        btnAddAtleta.setOnClickListener {
            val intent = Intent(this, CadastroAtleta::class.java)
            startActivity(intent)
        }

        // Logout
        btnSairTreinador.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarListaAtletas()
    }

    private fun atualizarListaAtletas() {
        // 1. Limpar a lista atual para não duplicar os cartões ao voltar do ecrã de registo
        containerAtletas.removeAllViews()

        // 2. Verificar se o banco de dados temporário está vazio
        if (DadosGlobais.listaAtletas.isEmpty()) {
            val tvVazio = TextView(this)
            tvVazio.text = "Ainda não tem atletas cadastrados."
            tvVazio.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
            tvVazio.textSize = 16f
            containerAtletas.addView(tvVazio)
            return
        }

        // 3. Preparar o "Inflador" de XML
        val inflater = LayoutInflater.from(this)

        // 4. Percorrer a lista de atletas do DadosGlobais
        for (atleta in DadosGlobais.listaAtletas) {

            val viewCartao = inflater.inflate(R.layout.item_atleta, containerAtletas, false)

            val tvNome = viewCartao.findViewById<TextView>(R.id.tvNomeItemAtleta)
            val tvDetalhes = viewCartao.findViewById<TextView>(R.id.tvDetalhesItemAtleta)

            tvNome.text = atleta.nome
            tvDetalhes.text = "Idade: ${atleta.idade} anos | Posição: ${atleta.posicao}"

            // 5. Ação de clique no cartão (Para cumprir o RF10)
            viewCartao.setOnClickListener {
                // Passa o ID do atleta clicado para a tela de Perfil
                val intent = Intent(this, PerfilAtleta::class.java)
                intent.putExtra("ATLETA_ID", atleta.id)
                startActivity(intent)
            }

            // 6. Adiciona o cartão finalizado ao contentor do ScrollView
            containerAtletas.addView(viewCartao)
        }
    }
}