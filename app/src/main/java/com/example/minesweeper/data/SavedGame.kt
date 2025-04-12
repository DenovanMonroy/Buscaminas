package com.example.minesweeper.data

import java.util.Date

data class SavedGame(
    val id: String,
    val difficulty: GameDifficulty,
    val score: Int,
    val timeElapsed: Long,
    val status: GameStatus,
    val savedAt: Date = Date(),
    val format: SaveFormat
)

enum class SaveFormat {
    XML,
    JSON
}