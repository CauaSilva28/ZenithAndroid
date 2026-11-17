package com.example.zenithapp

// Modelo do Atleta
data class Atleta(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nome: String,
    val idade: String,
    val peso: String,
    val altura: String,
    val posicao: String,
    val sexo: String
)

// Modelo do Treino
data class Treino(
    val id: String = java.util.UUID.randomUUID().toString(),
    val idAtleta: String,
    val titulo: String,
    val data: String,
    val exercicios: List<String> = emptyList(),
    var concluido: Boolean = false,
    var observacoes: String = ""
)