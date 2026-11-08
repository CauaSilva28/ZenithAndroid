package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuAtleta : AppCompatActivity() {

    private lateinit var containerTreinos: LinearLayout
    private lateinit var atletaLogado: Atleta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_atleta)

        val btnSairAtleta = findViewById<Button>(R.id.btnSairAtleta)
        containerTreinos = findViewById(R.id.containerTreinosAtleta)

        val tvNomeAtleta = findViewById<TextView>(R.id.tvNomeAtleta)
        val tvDetalhesAtleta = findViewById<TextView>(R.id.tvDetalhesAtleta)

        // 1. Receber o ID de quem fez login
        val idAtleta = intent.getStringExtra("ATLETA_ID") ?: ""
        val atletaEncontrado = DadosGlobais.buscarAtletaPorId(idAtleta)

        if (atletaEncontrado == null) {
            // Se algo der errado, expulsa de volta para o login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Define o atleta logado com base nos dados reais do login
        atletaLogado = atletaEncontrado

        // 2. Preencher o Resumo do Perfil (RF16)
        tvNomeAtleta.text = "Olá, ${atletaLogado.nome}"
        tvDetalhesAtleta.text = "Altura: ${atletaLogado.altura}m | Peso: ${atletaLogado.peso}kg"

        // 3. RF05 - Logout
        btnSairAtleta.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // Usamos o onResume para atualizar a lista sempre que o atleta voltar do Detalhe do Treino
    override fun onResume() {
        super.onResume()
        atualizarListaDeTreinosDoAtleta()
    }

    private fun atualizarListaDeTreinosDoAtleta() {
        containerTreinos.removeAllViews()

        // Filtrar apenas os treinos deste atleta específico
        val meusTreinos = DadosGlobais.listaTreinos.filter { it.idAtleta == atletaLogado.id }

        if (meusTreinos.isEmpty()) {
            val tvVazio = TextView(this)
            tvVazio.text = "Ainda não tem treinos programados."
            tvVazio.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
            tvVazio.textSize = 14f
            containerTreinos.addView(tvVazio)
            return
        }

        val inflater = LayoutInflater.from(this)

        for (treino in meusTreinos) {
            val viewCartaoTreino = inflater.inflate(R.layout.item_treino, containerTreinos, false)

            val tvTitulo = viewCartaoTreino.findViewById<TextView>(R.id.tvTituloItemTreino)
            val tvData = viewCartaoTreino.findViewById<TextView>(R.id.tvDataItemTreino)
            val tvStatus = viewCartaoTreino.findViewById<TextView>(R.id.tvStatusItemTreino)

            tvTitulo.text = treino.titulo
            tvData.text = "Data: ${treino.data}"

            if (treino.concluido) {
                tvStatus.text = "Concluído"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#050505"))
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#85A000"))
            } else {
                tvStatus.text = "Pendente"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#85A000"))
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#2D3A0A"))
            }

            viewCartaoTreino.setOnClickListener {
                val intent = Intent(this, DetalheTreino::class.java)
                intent.putExtra("TREINO_ID", treino.id)
                intent.putExtra("MODO_VISUALIZACAO", "ATLETA") // Avisa que é o atleta
                startActivity(intent)
            }

            containerTreinos.addView(viewCartaoTreino)
        }
    }
}