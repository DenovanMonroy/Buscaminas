package com.example.minesweeper.persistence

import android.content.Context
import android.content.SharedPreferences
import com.example.minesweeper.data.GameDifficulty
import com.example.minesweeper.data.GameStatus
import com.example.minesweeper.data.SaveFormat
import com.example.minesweeper.data.SavedGame
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

object StorageUtils {

    private const val PREF_NAME = "minesweeper_prefs"
    private const val KEY_SAVED_GAMES = "saved_games"
    private const val KEY_PREFERRED_FORMAT = "preferred_format"

    private val gson = Gson()

    // Guardar metadatos de las partidas guardadas
    fun saveMetadata(context: Context, savedGame: SavedGame) {
        val prefs = getPrefs(context)
        val savedGamesJson = prefs.getString(KEY_SAVED_GAMES, "[]")
        val type = object : TypeToken<MutableList<SavedGame>>() {}.type
        val savedGames = gson.fromJson<MutableList<SavedGame>>(savedGamesJson, type) ?: mutableListOf()

        // Actualizar o agregar el juego guardado
        val existingIndex = savedGames.indexOfFirst { it.id == savedGame.id }
        if (existingIndex >= 0) {
            savedGames[existingIndex] = savedGame
        } else {
            savedGames.add(savedGame)
        }

        prefs.edit().putString(KEY_SAVED_GAMES, gson.toJson(savedGames)).apply()
    }

    // Cargar metadatos de todas las partidas guardadas
    fun loadAllMetadata(context: Context): List<SavedGame> {
        val prefs = getPrefs(context)
        val savedGamesJson = prefs.getString(KEY_SAVED_GAMES, "[]")
        val type = object : TypeToken<List<SavedGame>>() {}.type
        return gson.fromJson(savedGamesJson, type) ?: emptyList()
    }

    // Eliminar metadatos de una partida
    fun deleteMetadata(context: Context, gameId: String) {
        val prefs = getPrefs(context)
        val savedGamesJson = prefs.getString(KEY_SAVED_GAMES, "[]")
        val type = object : TypeToken<MutableList<SavedGame>>() {}.type
        val savedGames = gson.fromJson<MutableList<SavedGame>>(savedGamesJson, type) ?: mutableListOf()

        savedGames.removeIf { it.id == gameId }

        prefs.edit().putString(KEY_SAVED_GAMES, gson.toJson(savedGames)).apply()
    }

    // Guardar formato preferido
    fun savePreferredFormat(context: Context, format: SaveFormat) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_PREFERRED_FORMAT, format.name).apply()
    }

    // Obtener formato preferido
    fun getPreferredFormat(context: Context): SaveFormat {
        val prefs = getPrefs(context)
        val formatName = prefs.getString(KEY_PREFERRED_FORMAT, SaveFormat.JSON.name)
        return try {
            SaveFormat.valueOf(formatName!!)
        } catch (e: Exception) {
            SaveFormat.JSON
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}