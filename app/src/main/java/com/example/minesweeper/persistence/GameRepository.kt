package com.example.minesweeper.persistence

import android.content.Context
import com.example.minesweeper.data.GameState
import com.example.minesweeper.data.SaveFormat
import com.example.minesweeper.data.SavedGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class GameRepository(private val context: Context) {

    private val xmlStorage = XmlGameStorage(context)
    private val jsonStorage = JsonGameStorage(context)

    // Guardar una partida en el formato especificado
    suspend fun saveGame(gameState: GameState, format: SaveFormat): Boolean {
        return withContext(Dispatchers.IO) {
            val result = when (format) {
                SaveFormat.XML -> xmlStorage.saveGame(gameState)
                SaveFormat.JSON -> jsonStorage.saveGame(gameState)
            }

            if (result) {
                // Guardar metadatos de la partida
                val savedGame = SavedGame(
                    id = gameState.id,
                    difficulty = gameState.difficulty,
                    score = gameState.score,
                    timeElapsed = gameState.timeElapsed,
                    status = gameState.status,
                    savedAt = Date(),
                    format = format
                )
                StorageUtils.saveMetadata(context, savedGame)
            }
            result
        }
    }

    // Cargar una partida guardada
    suspend fun loadGame(savedGame: SavedGame): GameState? {
        return withContext(Dispatchers.IO) {
            when (savedGame.format) {
                SaveFormat.XML -> xmlStorage.loadGame(savedGame.id)
                SaveFormat.JSON -> jsonStorage.loadGame(savedGame.id)
            }
        }
    }

    // Obtener todas las partidas guardadas
    suspend fun getAllSavedGames(): List<SavedGame> {
        return withContext(Dispatchers.IO) {
            StorageUtils.loadAllMetadata(context)
        }
    }

    // Eliminar una partida guardada
    suspend fun deleteGame(savedGame: SavedGame): Boolean {
        return withContext(Dispatchers.IO) {
            val result = when (savedGame.format) {
                SaveFormat.XML -> xmlStorage.deleteGame(savedGame.id)
                SaveFormat.JSON -> jsonStorage.deleteGame(savedGame.id)
            }
            if (result) {
                StorageUtils.deleteMetadata(context, savedGame.id)
            }
            result
        }
    }

    // Exportar una partida guardada a almacenamiento externo
    suspend fun exportGame(savedGame: SavedGame, outputFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            val gameFile = when (savedGame.format) {
                SaveFormat.XML -> xmlStorage.getGameFile(savedGame.id)
                SaveFormat.JSON -> jsonStorage.getGameFile(savedGame.id)
            }

            try {
                gameFile?.copyTo(outputFile, overwrite = true)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}