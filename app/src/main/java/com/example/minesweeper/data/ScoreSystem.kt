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

    // Calcular puntuación progresiva (para actualización en tiempo real)
    fun calculateProgressiveScore(
        timeElapsed: Long,
        difficulty: GameDifficulty,
        revealedCells: Int,
        totalNonMineCells: Int
    ): Int {
        val basePoints = revealedCells * 10  // 10 puntos por cada celda revelada
        val difficultyMultiplier = when (difficulty) {
            GameDifficulty.EASY -> 1
            GameDifficulty.HARD -> 2
        }
        // Bono por revelar celdas rápidamente
        val timeEfficiencyBonus = maxOf(0, 100 - (timeElapsed / 1000))

        return (basePoints * difficultyMultiplier) + timeEfficiencyBonus.toInt()
    }
}