package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PerfilAtleta : AppCompatActivity() {

    private var idDoAtletaAtual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_atleta)

        // 1. Receber o ID enviado pelo MenuTreinador
        idDoAtletaAtual = intent.getStringExtra("ATLETA_ID") ?: ""

        // Se por algum erro o ID vier vazio, fecha a tela
        if (idDoAtletaAtual.isEmpty()) {
            Toast.makeText(this, "Erro: Atleta não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Procurar o atleta no nosso banco de dados em memória
        val atleta = DadosGlobais.buscarAtletaPorId(idDoAtletaAtual)

        if (atleta == null) {
            Toast.makeText(this, "Erro: Dados do atleta perdidos.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Mapear os elementos visuais
        val tvNomeHeader = findViewById<TextView>(R.id.tvNomePerfilHeader)
        val tvIdadeSexo = findViewById<TextView>(R.id.tvIdadeSexo)
        val tvPesoAltura = findViewById<TextView>(R.id.tvPesoAltura)
        val tvPosicao = findViewById<TextView>(R.id.tvPosicao)

        val btnVoltar = findViewById<Button>(R.id.btnVoltarPerfil)
        val btnNovoTreino = findViewById<Button>(R.id.btnNovoTreino)

        // 4. Preencher a tela com os dados reais
        tvNomeHeader.text = atleta.nome
        tvIdadeSexo.text = "Idade: ${atleta.idade} anos | Sexo: ${atleta.sexo}"
        tvPesoAltura.text = "Peso: ${atleta.peso} kg | Altura: ${atleta.altura} m"
        tvPosicao.text = "Especialidade: ${atleta.posicao}"

        // Acão do botão voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // Acão de Criar Novo Treino (RF11)
        btnNovoTreino.setOnClickListener {
            // Vai para a tela de criação e PASSA o ID do atleta para que o treino saiba a quem pertence
            val intent = Intent(this, CriarTreino::class.java)
            intent.putExtra("ATLETA_ID", idDoAtletaAtual)
            startActivity(intent)
        }
    }
}