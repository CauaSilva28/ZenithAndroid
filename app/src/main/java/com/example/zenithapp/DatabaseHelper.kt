package com.example.zenithapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Banco local do Zenith.
 * Os dados ficam armazenados no dispositivo em que o aplicativo está instalado.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "zenith.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ATLETAS = "atletas"
        private const val TABLE_TREINOS = "treinos"
        private const val TABLE_EXERCICIOS = "exercicios"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_ATLETAS (
                id TEXT PRIMARY KEY NOT NULL,
                nome TEXT NOT NULL,
                idade TEXT NOT NULL,
                peso TEXT NOT NULL,
                altura TEXT NOT NULL,
                posicao TEXT NOT NULL,
                sexo TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_TREINOS (
                id TEXT PRIMARY KEY NOT NULL,
                atleta_id TEXT NOT NULL,
                titulo TEXT NOT NULL,
                data TEXT NOT NULL,
                concluido INTEGER NOT NULL DEFAULT 0,
                observacoes TEXT NOT NULL DEFAULT '',
                FOREIGN KEY(atleta_id) REFERENCES $TABLE_ATLETAS(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_EXERCICIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                treino_id TEXT NOT NULL,
                nome TEXT NOT NULL,
                ordem INTEGER NOT NULL,
                FOREIGN KEY(treino_id) REFERENCES $TABLE_TREINOS(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX idx_treinos_atleta ON $TABLE_TREINOS(atleta_id)")
        db.execSQL("CREATE INDEX idx_exercicios_treino ON $TABLE_EXERCICIOS(treino_id)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Quando a estrutura mudar, adicionar aqui migrações por versão.
        // Não apagar tabelas existentes para preservar os dados do usuário.
    }

    fun inserirAtleta(atleta: Atleta): Long {
        val values = ContentValues().apply {
            put("id", atleta.id)
            put("nome", atleta.nome)
            put("idade", atleta.idade)
            put("peso", atleta.peso)
            put("altura", atleta.altura)
            put("posicao", atleta.posicao)
            put("sexo", atleta.sexo)
        }
        return writableDatabase.insert(TABLE_ATLETAS, null, values)
    }

    fun listarAtletas(): List<Atleta> {
        val lista = mutableListOf<Atleta>()
        readableDatabase.query(
            TABLE_ATLETAS, null, null, null, null, null, "nome COLLATE NOCASE ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Atleta(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                        idade = cursor.getString(cursor.getColumnIndexOrThrow("idade")),
                        peso = cursor.getString(cursor.getColumnIndexOrThrow("peso")),
                        altura = cursor.getString(cursor.getColumnIndexOrThrow("altura")),
                        posicao = cursor.getString(cursor.getColumnIndexOrThrow("posicao")),
                        sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo"))
                    )
                )
            }
        }
        return lista
    }

    fun buscarAtletaPorId(id: String): Atleta? {
        readableDatabase.query(
            TABLE_ATLETAS, null, "id = ?", arrayOf(id), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return Atleta(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                    idade = cursor.getString(cursor.getColumnIndexOrThrow("idade")),
                    peso = cursor.getString(cursor.getColumnIndexOrThrow("peso")),
                    altura = cursor.getString(cursor.getColumnIndexOrThrow("altura")),
                    posicao = cursor.getString(cursor.getColumnIndexOrThrow("posicao")),
                    sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo"))
                )
            }
        }
        return null
    }

    fun excluirAtleta(id: String): Int =
        writableDatabase.delete(TABLE_ATLETAS, "id = ?", arrayOf(id))

    fun inserirTreino(treino: Treino): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("id", treino.id)
                put("atleta_id", treino.idAtleta)
                put("titulo", treino.titulo)
                put("data", treino.data)
                put("concluido", if (treino.concluido) 1 else 0)
                put("observacoes", treino.observacoes)
            }
            if (db.insert(TABLE_TREINOS, null, values) == -1L) return false

            treino.exercicios.forEachIndexed { index, nome ->
                val exerciseValues = ContentValues().apply {
                    put("treino_id", treino.id)
                    put("nome", nome)
                    put("ordem", index)
                }
                if (db.insert(TABLE_EXERCICIOS, null, exerciseValues) == -1L) {
                    return false
                }
            }
            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun listarTreinosDoAtleta(idAtleta: String): List<Treino> {
        val lista = mutableListOf<Treino>()
        readableDatabase.query(
            TABLE_TREINOS, null, "atleta_id = ?", arrayOf(idAtleta),
            null, null, "rowid DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                lista.add(treinoFromCursor(cursor, id))
            }
        }
        return lista
    }

    fun buscarTreinoPorId(id: String): Treino? {
        readableDatabase.query(
            TABLE_TREINOS, null, "id = ?", arrayOf(id), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) return treinoFromCursor(cursor, id)
        }
        return null
    }

    private fun treinoFromCursor(
        cursor: android.database.Cursor,
        id: String
    ): Treino {
        val exercicios = mutableListOf<String>()
        readableDatabase.query(
            TABLE_EXERCICIOS, arrayOf("nome"), "treino_id = ?",
            arrayOf(id), null, null, "ordem ASC"
        ).use { exerciseCursor ->
            while (exerciseCursor.moveToNext()) {
                exercicios.add(exerciseCursor.getString(0))
            }
        }

        return Treino(
            id = id,
            idAtleta = cursor.getString(cursor.getColumnIndexOrThrow("atleta_id")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
            data = cursor.getString(cursor.getColumnIndexOrThrow("data")),
            exercicios = exercicios,
            concluido = cursor.getInt(cursor.getColumnIndexOrThrow("concluido")) == 1,
            observacoes = cursor.getString(cursor.getColumnIndexOrThrow("observacoes"))
        )
    }

    fun atualizarConclusaoTreino(id: String, concluido: Boolean, observacoes: String): Int {
        val values = ContentValues().apply {
            put("concluido", if (concluido) 1 else 0)
            put("observacoes", observacoes)
        }
        return writableDatabase.update(TABLE_TREINOS, values, "id = ?", arrayOf(id))
    }

    fun excluirTreino(id: String): Int =
        writableDatabase.delete(TABLE_TREINOS, "id = ?", arrayOf(id))
}
