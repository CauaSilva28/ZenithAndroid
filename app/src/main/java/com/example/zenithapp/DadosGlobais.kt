package com.example.zenithapp

data class Atleta(
    val id: String = java.util.UUID.randomUUID().toString(), // Gera um ID único automático
    val nome: String,
    val idade: String,
    val peso: String,
    val altura: String,
    val posicao: String,
    val sexo: String
)

data class Treino(
    val id: String = java.util.UUID.randomUUID().toString(),
    val idAtleta: String,
    val titulo: String,
    val data: String,
    val exercicios: List<String>,
    var concluido: Boolean = false,
    var observacoes: String = ""
)

object DadosGlobais {
    val listaAtletas = mutableListOf<Atleta>()
    val listaTreinos = mutableListOf<Treino>()

    // Função para salvar um novo atleta
    fun adicionarAtleta(atleta: Atleta) {
        listaAtletas.add(atleta)
    }

    // Função para buscar um atleta específico pelo ID (útil para a tela de perfil)
    fun buscarAtletaPorId(id: String): Atleta? {
        return listaAtletas.find { it.id == id }
    }
}