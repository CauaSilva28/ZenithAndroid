package com.example.zenithapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroAtleta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_atleta)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarAtleta)

        val etNome = findViewById<EditText>(R.id.etNomeAtleta)
        val etIdade = findViewById<EditText>(R.id.etIdade)
        val etPeso = findViewById<EditText>(R.id.etPeso)
        val etAltura = findViewById<EditText>(R.id.etAltura)
        val etPosicao = findViewById<EditText>(R.id.etPosicao)
        val rgSexo = findViewById<RadioGroup>(R.id.rgSexo)

        btnVoltar.setOnClickListener {
            finish()
        }

        val db = DatabaseHelper(this)

        // Ação do botão de Salvar
        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val idade = etIdade.text.toString().trim()
            val peso = etPeso.text.toString().trim()
            val altura = etAltura.text.toString().trim()
            val posicao = etPosicao.text.toString().trim()

            val sexo = when (rgSexo.checkedRadioButtonId) {
                R.id.rbMasculino -> "Masculino"
                R.id.rbFeminino -> "Feminino"
                else -> ""
            }

            // 1. Validação de campos vazios
            if (nome.isEmpty() || idade.isEmpty() || peso.isEmpty() || altura.isEmpty() || posicao.isEmpty() || sexo.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Validação de Nome + Sobrenome (pelo menos 2 palavras)
            val partesDoNome = nome.split(" ").filter { it.isNotEmpty() }
            if (partesDoNome.size < 2) {
                Toast.makeText(this, "Por favor, insira o nome e sobrenome (nome e sobrenome).", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 3. Verificação de Duplicidade no SQLite
            val todosAtletas = db.listarAtletas()
            val jaExiste = todosAtletas.any { atletaExistente ->
                atletaExistente.nome.equals(nome, ignoreCase = true) && atletaExistente.idade == idade
            }

            if (jaExiste) {
                Toast.makeText(
                    this,
                    "Já existe um atleta cadastrado com o nome '$nome' e $idade anos!",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // 4. Salvar novo atleta no banco local
            val novoAtleta = Atleta(
                nome = nome,
                idade = idade,
                peso = peso,
                altura = altura,
                posicao = posicao,
                sexo = sexo
            )

            db.inserirAtleta(novoAtleta)

            Toast.makeText(this, "Atleta $nome cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}