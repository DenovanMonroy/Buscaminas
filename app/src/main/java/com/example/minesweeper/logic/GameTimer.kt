package com.example.minesweeper.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class GameTimer(private val initialTimeElapsed: Long = 0) {

    var timeElapsed by mutableStateOf(initialTimeElapsed)
        private set

    private var startTime: Long = 0
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        if (timerJob != null) return

        startTime = System.currentTimeMillis() - timeElapsed
        timerJob = scope.launch {
            while (true) {
                timeElapsed = System.currentTimeMillis() - startTime
                delay(1000) // Update every second
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
        timerJob = null
    }

    fun reset() {
        pause()
        timeElapsed = 0
    }

    fun formatTime(): String {
        val seconds = (timeElapsed / 1000) % 60
        val minutes = (timeElapsed / (1000 * 60)) % 60
        val hours = (timeElapsed / (1000 * 60 * 60))
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}