package com.example.zenithapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetalheTreino : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_treino)

        // 1. Receber os extras da Intent
        val idTreino = intent.getStringExtra("TREINO_ID") ?: ""
        val modoAtleta = intent.getStringExtra("MODO_VISUALIZACAO") == "ATLETA"

        if (idTreino.isEmpty()) {
            Toast.makeText(this, "Erro: Treino não identificado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Instanciar o Banco de Dados e buscar o treino direto do SQLite
        val db = DatabaseHelper(this)
        val treino = db.buscarTreinoPorId(idTreino)

        if (treino == null) {
            Toast.makeText(this, "Erro: Treino não encontrado no banco de dados.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Mapear os elementos visuais da tela
        val btnVoltar = findViewById<Button>(R.id.btnVoltarDetalhe)
        val tvTitulo = findViewById<TextView>(R.id.tvTituloDetalhe)
        val tvData = findViewById<TextView>(R.id.tvDataDetalhe)
        val tvStatus = findViewById<TextView>(R.id.tvStatusDetalhe)
        val containerExercicios = findViewById<LinearLayout>(R.id.containerExerciciosChecklist)
        val etObservacoes = findViewById<EditText>(R.id.etObservacoesDetalhe)
        val btnConcluir = findViewById<Button>(R.id.btnConcluirTreino)
        val btnExcluirTreino = findViewById<Button>(R.id.btnExcluirTreino)

        // Se quem abriu foi o atleta, esconde o botão de excluir
        if (modoAtleta) {
            btnExcluirTreino.visibility = View.GONE
        }

        // 4. Preencher os dados do cabeçalho
        tvTitulo.text = treino.titulo
        tvData.text = "Data: ${treino.data}"
        etObservacoes.setText(treino.observacoes)

        if (treino.concluido) {
            tvStatus.text = "CONCLUÍDO"
            tvStatus.setTextColor(Color.parseColor("#050505"))
            tvStatus.setBackgroundColor(Color.parseColor("#85A000"))
            btnConcluir.text = "SALVAR ALTERAÇÕES"
        }

        // 5. Gerar as CheckBoxes dinamicamente para os exercícios vindos do SQLite (RF18)
        for (exercicio in treino.exercicios) {
            val checkBox = CheckBox(this)
            checkBox.text = exercicio
            checkBox.setTextColor(Color.WHITE)
            checkBox.textSize = 16f
            checkBox.setPadding(16, 16, 16, 16)
            checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#85A000"))

            if (treino.concluido) {
                checkBox.isChecked = true
            }

            containerExercicios.addView(checkBox)
        }

        // Ação do Botão Voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // 6. Salvar Alterações / Concluir Treino (RF18 e RF19)
        btnConcluir.setOnClickListener {
            val obsTexto = etObservacoes.text.toString().trim()
            db.atualizarConclusaoTreino(idTreino, true, obsTexto)
            Toast.makeText(this, "Treino atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 7. Excluir Treino com Confirmação (RF13)
        btnExcluirTreino.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Excluir Treino")
            builder.setMessage("Tem certeza que deseja excluir o treino '${treino.titulo}'?")

            builder.setPositiveButton("Excluir") { _, _ ->
                db.excluirTreino(idTreino)
                Toast.makeText(this, "Treino excluído.", Toast.LENGTH_SHORT).show()
                finish()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }
}