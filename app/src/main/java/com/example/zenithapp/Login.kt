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

        val db = DatabaseHelper(this)

        btnEntrar.setOnClickListener {
            val usuarioDigitado = etUsuario.text.toString().trim()
            val senhaDigitada = etSenha.text.toString().trim()

            tvErroLogin.visibility = View.GONE

            // 1. Acesso do Treinador (Fixo)
            if (usuarioDigitado == "treinador" && senhaDigitada == "123") {
                startActivity(Intent(this, MenuTreinador::class.java))
                finish()
                return@setOnClickListener
            }

            // 2. Acesso Dinâmico do Atleta no SQLite
            val db = DatabaseHelper(this)
            val todosAtletas = db.listarAtletas()

            val atletaEncontrado = todosAtletas.find { atleta ->
                // Remove todos os espaços do nome completo (ex: "Cauã Silva Santos" -> "cauasilvasantos")
                val usuarioEsperado = atleta.nome.replace(" ", "").lowercase()

                // Cria a senha (ex: "cauasilvasantos18")
                val senhaEsperada = "${usuarioEsperado}${atleta.idade}"

                // Compara ignorando maiúsculas e minúsculas
                usuarioDigitado.lowercase() == usuarioEsperado &&
                        senhaDigitada.lowercase() == senhaEsperada
            }

            // 3. Redirecionamento ou Erro
            if (atletaEncontrado != null) {
                val intent = Intent(this, MenuAtleta::class.java)
                intent.putExtra("ATLETA_ID", atletaEncontrado.id)
                startActivity(intent)
                finish()
            } else {
                tvErroLogin.visibility = View.VISIBLE
            }
        }
    }
}