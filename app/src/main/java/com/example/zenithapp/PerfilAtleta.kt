package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PerfilAtleta : AppCompatActivity() {

    private var idDoAtletaAtual: String = ""
    private lateinit var containerTreinos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_atleta)

        idDoAtletaAtual = intent.getStringExtra("ATLETA_ID") ?: ""

        if (idDoAtletaAtual.isEmpty()) {
            Toast.makeText(this, "Erro: Atleta não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val atleta = DadosGlobais.buscarAtletaPorId(idDoAtletaAtual)

        if (atleta == null) {
            Toast.makeText(this, "Erro: Dados do atleta perdidos.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mapear elementos da interface
        val tvNomeHeader = findViewById<TextView>(R.id.tvNomePerfilHeader)
        val tvIdadeSexo = findViewById<TextView>(R.id.tvIdadeSexo)
        val tvPesoAltura = findViewById<TextView>(R.id.tvPesoAltura)
        val tvPosicao = findViewById<TextView>(R.id.tvPosicao)

        val btnVoltar = findViewById<Button>(R.id.btnVoltarPerfil)
        val btnNovoTreino = findViewById<Button>(R.id.btnNovoTreino)
        val btnExcluirAtleta = findViewById<Button>(R.id.btnExcluirAtleta)

        containerTreinos = findViewById(R.id.containerTreinosPerfil)

        // Preencher dados do atleta
        tvNomeHeader.text = atleta.nome
        tvIdadeSexo.text = "Idade: ${atleta.idade} anos | Sexo: ${atleta.sexo}"
        tvPesoAltura.text = "Peso: ${atleta.peso} kg | Altura: ${atleta.altura} m"
        tvPosicao.text = "Especialidade: ${atleta.posicao}"

        btnVoltar.setOnClickListener { finish() }

        btnNovoTreino.setOnClickListener {
            val intent = Intent(this, CriarTreino::class.java)
            intent.putExtra("ATLETA_ID", idDoAtletaAtual)
            startActivity(intent)
        }

        btnExcluirAtleta.setOnClickListener {
            // Cria a caixa de diálogo de confirmação
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Excluir Atleta")
            builder.setMessage("Tem certeza que deseja excluir ${atleta.nome}? Todos os treinos vinculados também serão perdidos.")

            builder.setPositiveButton("Sim, excluir") { dialog, which ->
                // 1. Remove os treinos associados a este atleta
                DadosGlobais.listaTreinos.removeAll { it.idAtleta == idDoAtletaAtual }

                // 2. Remove o próprio atleta da lista
                DadosGlobais.listaAtletas.removeAll { it.id == idDoAtletaAtual }

                Toast.makeText(this, "Atleta excluído.", Toast.LENGTH_SHORT).show()
                finish() // Fecha o perfil e volta para a lista do treinador
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss() // Apenas fecha o alerta
            }

            // Personaliza a cor do alerta para não ficar com o branco padrão se quiser, ou apenas exibe
            builder.show()
        }
    }

    // Usamos o onResume para atualizar a lista sempre que voltarmos do ecrã de "Criar Treino"
    override fun onResume() {
        super.onResume()
        if (idDoAtletaAtual.isNotEmpty()) {
            atualizarListaDeTreinos()
        }
    }

    private fun atualizarListaDeTreinos() {
        // Limpar a lista atual para não duplicar itens
        containerTreinos.removeAllViews()

        // Filtrar no nosso banco de dados apenas os treinos que pertencem a ESTE atleta
        val treinosDoAtleta = DadosGlobais.listaTreinos.filter { it.idAtleta == idDoAtletaAtual }

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

            // Ajusta o estilo visual dependendo de o treino estar concluído ou não
            if (treino.concluido) {
                tvStatus.text = "Concluído"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#050505"))
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#85A000"))
            } else {
                tvStatus.text = "Pendente"
                tvStatus.setTextColor(android.graphics.Color.parseColor("#85A000"))
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#2D3A0A"))
            }

            // Ação de clique no treino (Abre o Detalhe)
            viewCartaoTreino.setOnClickListener {
                val intent = Intent(this, DetalheTreino::class.java)
                intent.putExtra("TREINO_ID", treino.id) // Passa o ID deste treino específico
                startActivity(intent)
            }

            containerTreinos.addView(viewCartaoTreino)
        }
    }
}