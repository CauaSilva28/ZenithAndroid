package com.example.zenithapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuAtleta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_atleta)

        val btnSairAtleta = findViewById<Button>(R.id.btnSairAtleta)

        btnSairAtleta.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            // Estas flags limpam o histórico para que o utilizador não possa usar o botão "Voltar" do telemóvel para regressar à conta
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}