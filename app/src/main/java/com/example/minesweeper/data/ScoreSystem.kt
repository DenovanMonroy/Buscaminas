package com.example.minesweeper.data

object ScoreSystem {

    // Calcular puntuación basada en tiempo, dificultad y celdas descubiertas
    fun calculateScore(
        timeElapsed: Long,
        difficulty: GameDifficulty,
        revealedCells: Int,
        totalNonMineCells: Int
    ): Int {
        val timeBonus = maxOf(0, 1000 - (timeElapsed / 1000))
        val difficultyMultiplier = when (difficulty) {
            GameDifficulty.EASY -> 1
            GameDifficulty.HARD -> 2
        }
        val completionPercentage = revealedCells / totalNonMineCells.toFloat()

        return ((timeBonus * difficultyMultiplier) * completionPercentage).toInt()
    }
}