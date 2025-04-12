package com.example.minesweeper.data

import java.util.Date

data class Cell(
    val x: Int,
    val y: Int,
    val hasMine: Boolean = false,
    val isRevealed: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMines: Int = 0
)

enum class GameDifficulty(val boardSize: Int, val mineCount: Int) {
    EASY(8, 10),
    HARD(15, 40)
}

enum class GameStatus {
    ONGOING,
    WON,
    LOST
}

data class GameState(
    val difficulty: GameDifficulty = GameDifficulty.EASY,
    val board: List<List<Cell>> = emptyList(),
    val status: GameStatus = GameStatus.ONGOING,
    val score: Int = 0,
    val timeElapsed: Long = 0L,
    val startTime: Date? = null,
    val id: String = System.currentTimeMillis().toString()
)