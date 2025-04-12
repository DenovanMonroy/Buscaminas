package com.example.minesweeper.persistence

import android.content.Context
import com.example.minesweeper.data.GameState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JsonGameStorage(private val context: Context) {

    private val gson = Gson()
    private val gamesDir = File(context.filesDir, "games/json")

    init {
        if (!gamesDir.exists()) {
            gamesDir.mkdirs()
        }
    }

    suspend fun saveGame(gameState: GameState): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(gamesDir, "${gameState.id}.json")
                FileWriter(file).use { writer ->
                    gson.toJson(gameState, writer)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun loadGame(gameId: String): GameState? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(gamesDir, "$gameId.json")
                if (!file.exists()) return@withContext null

                FileReader(file).use { reader ->
                    gson.fromJson(reader, GameState::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun getGameFile(gameId: String): File? {
        val file = File(gamesDir, "$gameId.json")
        return if (file.exists()) file else null
    }

    suspend fun deleteGame(gameId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(gamesDir, "$gameId.json")
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        }
    }
}