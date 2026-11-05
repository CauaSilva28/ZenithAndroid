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
            val usuario = etUsuario.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            tvErroLogin.visibility = View.GONE

            if (usuario == "treinador" && senha == "treinador") {
                val intent = Intent(this, MenuTreinador::class.java)
                startActivity(intent)
                finish()

            } else if (usuario == "atleta" && senha == "atleta") {
                val intent = Intent(this, MenuAtleta::class.java)
                startActivity(intent)
                finish()

            } else {
                tvErroLogin.visibility = View.VISIBLE
            }
        }
    }
}