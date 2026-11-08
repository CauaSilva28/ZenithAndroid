package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val tvErroLogin = findViewById<TextView>(R.id.tvErroLogin)

        btnEntrar.setOnClickListener {
            val usuarioDigitado = etUsuario.text.toString().trim()
            val senhaDigitada = etSenha.text.toString().trim()

            tvErroLogin.visibility = View.GONE

            // 1. Validação do Treinador (Acesso Fixo)
            if (usuarioDigitado == "treinador" && senhaDigitada == "treinador") {
                val intent = Intent(this, MenuTreinador::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            // 2. Validação Dinâmica do Atleta
            // Procura na nossa memória algum atleta que combine com a regra (Primeiro Nome + Idade)
            val atletaEncontrado = DadosGlobais.listaAtletas.find { atleta ->

                // Pega apenas o primeiro nome (ex: de "Cauã Silva", pega "Cauã")
                val primeiroNome = atleta.nome.split(" ")[0]

                // Cria a senha esperada (ex: "Cauã18")
                val senhaEsperada = "$primeiroNome${atleta.idade}"

                // Compara ignorando maiúsculas e minúsculas (ignoreCase = true)
                usuarioDigitado.equals(primeiroNome, ignoreCase = true) &&
                        senhaDigitada.equals(senhaEsperada, ignoreCase = true)
            }

            // 3. Resultado do Login
            if (atletaEncontrado != null) {
                // Sucesso! Envia o ID deste atleta específico para a próxima tela
                val intent = Intent(this, MenuAtleta::class.java)
                intent.putExtra("ATLETA_ID", atletaEncontrado.id)
                startActivity(intent)
                finish()
            } else {
                // RF04 - Recusar acesso e mostrar erro
                tvErroLogin.visibility = View.VISIBLE
            }
        }
    }
}