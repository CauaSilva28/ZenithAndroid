package com.example.zenithapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuAtleta : AppCompatActivity() {

    private lateinit var containerTreinos: LinearLayout
    private lateinit var atletaLogado: Atleta
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_atleta)

        // 1. Inicializar o banco de dados SQLite
        db = DatabaseHelper(this)

        val btnSairAtleta = findViewById<Button>(R.id.btnSairAtleta)
        containerTreinos = findViewById(R.id.containerTreinosAtleta)

        val tvNomeAtleta = findViewById<TextView>(R.id.tvNomeAtleta)
        val tvDetalhesAtleta = findViewById<TextView>(R.id.tvDetalhesAtleta)

        // 2. Receber o ID de quem fez login e buscar no SQLite
        val idAtleta = intent.getStringExtra("ATLETA_ID") ?: ""
        val atletaEncontrado = db.buscarAtletaPorId(idAtleta)

        if (atletaEncontrado == null) {
            // Se não encontrar no banco, desloga por segurança
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        atletaLogado = atletaEncontrado

        // 3. Preencher o Resumo do Perfil (RF16)
        tvNomeAtleta.text = "Olá, ${atletaLogado.nome}"
        tvDetalhesAtleta.text = "Altura: ${atletaLogado.altura}m | Peso: ${atletaLogado.peso}kg"

        // 4. RF05 - Logout
        btnSairAtleta.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarListaDeTreinosDoAtleta()
    }

    private fun atualizarListaDeTreinosDoAtleta() {
        containerTreinos.removeAllViews()

        // Busca apenas os treinos vinculados a este atleta no SQLite
        val meusTreinos = db.listarTreinosDoAtleta(atletaLogado.id)

        if (meusTreinos.isEmpty()) {
            val tvVazio = TextView(this)
            tvVazio.text = "Ainda não tem treinos programados."
            tvVazio.setTextColor(Color.parseColor("#AAAAAA"))
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
                tvStatus.setTextColor(Color.parseColor("#050505"))
                tvStatus.setBackgroundColor(Color.parseColor("#85A000"))
            } else {
                tvStatus.text = "Pendente"
                tvStatus.setTextColor(Color.parseColor("#85A000"))
                tvStatus.setBackgroundColor(Color.parseColor("#2D3A0A"))
            }

            viewCartaoTreino.setOnClickListener {
                val intent = Intent(this, DetalheTreino::class.java)
                intent.putExtra("TREINO_ID", treino.id)
                intent.putExtra("MODO_VISUALIZACAO", "ATLETA")
                startActivity(intent)
            }

            containerTreinos.addView(viewCartaoTreino)
        }
    }
}