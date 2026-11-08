package com.example.zenithapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetalheTreino : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_treino)

        // 1. Receber o ID do treino clicado
        val idTreino = intent.getStringExtra("TREINO_ID") ?: ""

        if (idTreino.isEmpty()) {
            Toast.makeText(this, "Erro: Treino não identificado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Localizar o treino específico na lista global
        val treino = DadosGlobais.listaTreinos.find { it.id == idTreino }

        if (treino == null) {
            Toast.makeText(this, "Erro: Treino não encontrado no sistema.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Mapear os elementos visuais
        val btnVoltar = findViewById<Button>(R.id.btnVoltarDetalhe)
        val tvTitulo = findViewById<TextView>(R.id.tvTituloDetalhe)
        val tvData = findViewById<TextView>(R.id.tvDataDetalhe)
        val tvStatus = findViewById<TextView>(R.id.tvStatusDetalhe)
        val containerExercicios = findViewById<LinearLayout>(R.id.containerExerciciosChecklist)
        val etObservacoes = findViewById<EditText>(R.id.etObservacoesDetalhe)
        val btnConcluir = findViewById<Button>(R.id.btnConcluirTreino)
        val btnExcluirTreino = findViewById<Button>(R.id.btnExcluirTreino)

        val modoAtleta = intent.getStringExtra("MODO_VISUALIZACAO") == "ATLETA"

        // Se quem abriu foi o atleta, esconde o botão de excluir
        if (modoAtleta) {
            btnExcluirTreino.visibility = android.view.View.GONE
        }

        // 4. Preencher os dados do cabeçalho
        tvTitulo.text = treino.titulo
        tvData.text = "Data: ${treino.data}"
        etObservacoes.setText(treino.observacoes) // Carrega as observações já existentes, se houver

        if (treino.concluido) {
            tvStatus.text = "CONCLUÍDO"
            tvStatus.setTextColor(Color.parseColor("#050505"))
            tvStatus.setBackgroundColor(Color.parseColor("#85A000"))
            btnConcluir.text = "SALVAR ALTERAÇÕES"
        }

        // 5. Gerar as CheckBoxes dinamicamente para cada exercício (RF18)
        for (exercicio in treino.exercicios) {
            val checkBox = CheckBox(this)
            checkBox.text = exercicio
            checkBox.setTextColor(Color.WHITE)
            checkBox.textSize = 16f
            checkBox.setPadding(16, 16, 16, 16)

            // Define a cor da "caixinha" de seleção para o verde lima do Zenith
            checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#85A000"))

            // Se o treino já estava concluído, deixamos as caixas marcadas
            if (treino.concluido) {
                checkBox.isChecked = true
            }

            // Adiciona a CheckBox ao ecrã
            containerExercicios.addView(checkBox)
        }

        // Ação do Botão Voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // 6. Ação de Concluir / Salvar Treino
        btnConcluir.setOnClickListener {
            // Atualiza o estado do treino na memória (certifique-se que no DadosGlobais.kt são 'var')
            treino.concluido = true
            treino.observacoes = etObservacoes.text.toString().trim()

            Toast.makeText(this, "Treino atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            finish() // Retorna ao Perfil do Atleta (ou Menu)
        }

        btnExcluirTreino.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Excluir Treino")
            builder.setMessage("Tem certeza que deseja excluir o treino '${treino.titulo}'?")

            builder.setPositiveButton("Excluir") { dialog, which ->
                // Remove o treino da lista global
                DadosGlobais.listaTreinos.removeAll { it.id == idTreino }

                Toast.makeText(this, "Treino excluído.", Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela de detalhe e volta para o perfil do atleta
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
    }
}