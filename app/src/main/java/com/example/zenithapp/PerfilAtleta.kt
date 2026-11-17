package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PerfilAtleta : AppCompatActivity() {

    private var idDoAtletaAtual: String = ""
    private lateinit var containerTreinos: LinearLayout
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_atleta)

        idDoAtletaAtual = intent.getStringExtra("ATLETA_ID") ?: ""

        if (idDoAtletaAtual.isEmpty()) {
            Toast.makeText(this, "Erro: Atleta não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Instancia o banco SQLite
        db = DatabaseHelper(this)

        // Busca o atleta direto do banco
        val atleta = db.buscarAtletaPorId(idDoAtletaAtual)

        if (atleta == null) {
            Toast.makeText(this, "Erro: Dados do atleta não encontrados no banco.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mapear elementos da interface
        val tvNomeHeader = findViewById<TextView>(R.id.tvNomePerfilHeader)
        val tvIdadeSexo = findViewById<TextView>(R.id.tvIdadeSexo)
        val tvPesoAltura = findViewById<TextView>(R.id.tvPesoAltura)
        val tvPosicao = findViewById<TextView>(R.id.tvPosicao)

        // Novas TextViews de Acesso
        val tvUsuarioAtleta = findViewById<TextView>(R.id.tvUsuarioAtleta)
        val tvSenhaAtleta = findViewById<TextView>(R.id.tvSenhaAtleta)

        val btnVoltar = findViewById<Button>(R.id.btnVoltarPerfil)
        val btnNovoTreino = findViewById<Button>(R.id.btnNovoTreino)
        val btnExcluirAtleta = findViewById<Button>(R.id.btnExcluirAtleta)

        containerTreinos = findViewById(R.id.containerTreinosPerfil)

        // Preencher dados do atleta
        tvNomeHeader.text = atleta.nome
        tvIdadeSexo.text = "Idade: ${atleta.idade} anos | Sexo: ${atleta.sexo}"
        tvPesoAltura.text = "Peso: ${atleta.peso} kg | Altura: ${atleta.altura} m"
        tvPosicao.text = "Especialidade: ${atleta.posicao}"

        // Gera as credenciais com base na regra de Login (Nome sem espaços + Idade)
        val usuarioFormatado = atleta.nome.replace(" ", "").lowercase()
        val senhaFormatada = "${usuarioFormatado}${atleta.idade}"

        // Exibe no perfil para o treinador
        tvUsuarioAtleta.text = "Usuário: $usuarioFormatado"
        tvSenhaAtleta.text = "Senha: $senhaFormatada"

        btnVoltar.setOnClickListener { finish() }

        btnNovoTreino.setOnClickListener {
            val intent = Intent(this, CriarTreino::class.java)
            intent.putExtra("ATLETA_ID", idDoAtletaAtual)
            startActivity(intent)
        }

        // Excluir Atleta com Confirmação via SQLite (RF08)
        btnExcluirAtleta.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Excluir Atleta")
            builder.setMessage("Tem certeza que deseja excluir ${atleta.nome}? Todos os treinos vinculados também serão apagados.")

            builder.setPositiveButton("Sim, excluir") { _, _ ->
                // O ON DELETE CASCADE do SQLite exclui treinos e exercícios automaticamente
                db.excluirAtleta(idDoAtletaAtual)
                Toast.makeText(this, "Atleta excluído.", Toast.LENGTH_SHORT).show()
                finish()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (idDoAtletaAtual.isNotEmpty()) {
            atualizarListaDeTreinos()
        }
    }

    private fun atualizarListaDeTreinos() {
        containerTreinos.removeAllViews()

        // Busca a lista de treinos deste atleta vinda do SQLite
        val treinosDoAtleta = db.listarTreinosDoAtleta(idDoAtletaAtual)

        if (treinosDoAtleta.isEmpty()) {
            val tvVazio = TextView(this)
            tvVazio.text = "Nenhum treino programado."
            tvVazio.setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
            tvVazio.textSize = 14f
            containerTreinos.addView(tvVazio)
            return
        }

        val inflater = LayoutInflater.from(this)

        for (treino in treinosDoAtleta) {
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
                startActivity(intent)
            }

            containerTreinos.addView(viewCartaoTreino)
        }
    }
}