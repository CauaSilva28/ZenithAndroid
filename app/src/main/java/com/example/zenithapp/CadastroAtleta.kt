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

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val idade = etIdade.text.toString().trim()
            val peso = etPeso.text.toString().trim()
            val altura = etAltura.text.toString().trim()
            val posicao = etPosicao.text.toString().trim()

            // Verifica qual o botão de rádio selecionado
            val sexo = when (rgSexo.checkedRadioButtonId) {
                R.id.rbMasculino -> "Masculino"
                R.id.rbFeminino -> "Feminino"
                else -> ""
            }

            // Validação de campos vazios
            if (nome.isEmpty() || idade.isEmpty() || peso.isEmpty() || altura.isEmpty() || posicao.isEmpty() || sexo.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Cria o objeto do Atleta com os dados digitados
            val novoAtleta = Atleta(
                nome = nome,
                idade = idade,
                peso = peso,
                altura = altura,
                posicao = posicao,
                sexo = sexo
            )

            // 2. Salva no nosso banco de dados em memória
            DadosGlobais.adicionarAtleta(novoAtleta)

            // 3. Mostra a mensagem de sucesso e fecha a tela
            Toast.makeText(this, "Atleta $nome cadastrado com sucesso!", Toast.LENGTH_LONG).show()
            finish()

            Toast.makeText(this, "Atleta $nome cadastrado com sucesso!", Toast.LENGTH_LONG).show()

            // Após salvar, fechamos o formulário
            finish()
        }
    }
}