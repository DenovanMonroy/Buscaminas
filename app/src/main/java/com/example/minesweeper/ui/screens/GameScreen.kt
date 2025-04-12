package com.example.minesweeper.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.minesweeper.data.Cell
import com.example.minesweeper.data.GameDifficulty
import com.example.minesweeper.data.GameState
import com.example.minesweeper.data.GameStatus
import com.example.minesweeper.data.SaveFormat
import com.example.minesweeper.logic.GameLogic
import com.example.minesweeper.logic.GameTimer
import com.example.minesweeper.persistence.GameRepository
import com.example.minesweeper.persistence.StorageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameId: String?,
    onBackClick: () -> Unit,
    onGameSaved: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val gameLogic = remember { GameLogic() }
    val gameRepository = remember { GameRepository(context) }

    // States
    var gameState by remember { mutableStateOf<GameState?>(null) }
    var difficulty by remember { mutableStateOf(GameDifficulty.EASY) }
    var saveDialogVisible by remember { mutableStateOf(false) }
    var gameOverDialogVisible by remember { mutableStateOf(false) }
    var difficultyDialogVisible by remember { mutableStateOf(gameId == null) } // Show if new game
    var flagMode by remember { mutableStateOf(false) }

    // Game timer
    val gameTimer = remember(gameState?.id) {
        GameTimer(gameState?.timeElapsed ?: 0)
    }

    // Actualizar tiempo periódicamente mientras el juego está en curso
    LaunchedEffect(gameState?.id) {
        if (gameState?.status == GameStatus.ONGOING) {
            gameTimer.start()
            while (gameState?.status == GameStatus.ONGOING) {
                delay(1000) // Actualizar cada segundo
                gameState = gameState?.copy(timeElapsed = gameTimer.timeElapsed)
            }
        }
    }

    // Efecto para detener el timer cuando el juego no está en curso
    LaunchedEffect(gameState?.status) {
        if (gameState?.status != GameStatus.ONGOING) {
            gameTimer.pause()
        }
    }

    // Effect to load existing game
    LaunchedEffect(gameId) {
        if (gameId != null) {
            // Load game
            val savedGames = gameRepository.getAllSavedGames()
            val savedGame = savedGames.find { it.id == gameId }

            savedGame?.let {
                gameRepository.loadGame(it)?.let { loadedGame ->
                    gameState = loadedGame
                    if (loadedGame.status == GameStatus.ONGOING) {
                        gameTimer.start()
                    }
                }
            }
        }
    }

    // Create new game when difficulty is selected
    fun startNewGame(selectedDifficulty: GameDifficulty) {
        gameState = gameLogic.createNewGame(selectedDifficulty)
        difficulty = selectedDifficulty
        gameTimer.reset()
        gameTimer.start()
    }

    // Save game function
    fun saveCurrentGame() {
        gameState?.let { state ->
            // Update time elapsed in game state
            val updatedState = state.copy(timeElapsed = gameTimer.timeElapsed)

            coroutineScope.launch {
                val format = StorageUtils.getPreferredFormat(context)
                val saved = gameRepository.saveGame(updatedState, format)
                if (saved) {
                    onGameSaved()
                }
            }
        }
    }

    // Handle cell click
    fun handleCellClick(cell: Cell) {
        if (gameState?.status != GameStatus.ONGOING) return

        gameState?.let {
            gameState = if (flagMode) {
                gameLogic.toggleFlag(it, cell.x, cell.y)
            } else {
                val newState = gameLogic.revealCell(it, cell.x, cell.y)
                if (newState.status != GameStatus.ONGOING) {
                    gameTimer.pause()
                    gameOverDialogVisible = true
                }
                newState
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscaminas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Flag toggle button
                    IconButton(
                        onClick = { flagMode = !flagMode }
                    ) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = "Modo Bandera",
                            tint = if (flagMode) Color.Red else Color.Gray
                        )
                    }

                    // Save button
                    Button(
                        onClick = { saveDialogVisible = true }
                    ) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game info
            GameInfo(
                score = gameState?.score ?: 0,
                timeFormatted = gameTimer.formatTime(),
                difficulty = gameState?.difficulty?.name ?: difficulty.name
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Game board
            gameState?.let { state ->
                GameBoard(
                    board = state.board,
                    onCellClick = { cell -> handleCellClick(cell) }
                )
            }

            // Difficulty selection dialog
            if (difficultyDialogVisible) {
                DifficultyDialog(
                    onDifficultySelected = { selectedDifficulty ->
                        startNewGame(selectedDifficulty)
                        difficultyDialogVisible = false
                    }
                )
            }

            // Save dialog
            if (saveDialogVisible) {
                SaveGameDialog(
                    context = context,
                    onFormatSelected = { format ->
                        StorageUtils.savePreferredFormat(context, format)
                        saveCurrentGame()
                        saveDialogVisible = false
                    },
                    onDismiss = { saveDialogVisible = false }
                )
            }

            // Game over dialog
            if (gameOverDialogVisible) {
                GameOverDialog(
                    gameState = gameState,
                    onNewGame = {
                        difficultyDialogVisible = true
                        gameOverDialogVisible = false
                    },
                    onSaveGame = {
                        saveDialogVisible = true
                        gameOverDialogVisible = false
                    },
                    onDismiss = { gameOverDialogVisible = false }
                )
            }
        }
    }
}

@Composable
fun GameInfo(
    score: Int,
    timeFormatted: String,
    difficulty: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PUNTUACIÓN", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TIEMPO", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NIVEL", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = difficulty,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GameBoard(
    board: List<List<Cell>>,
    onCellClick: (Cell) -> Unit
) {
    val size = board.size
    val cellSize = min(320, 400) / size

    Box(
        modifier = Modifier
            .padding(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(size),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(board.flatten()) { cell ->
                CellView(
                    cell = cell,
                    onClick = { onCellClick(cell) },
                    modifier = Modifier
                        .size(cellSize.dp)
                        .padding(1.dp)
                )
            }
        }
    }
}

@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                when {
                    cell.isRevealed && cell.hasMine -> Color.Red
                    cell.isRevealed -> Color(0xFFD0D0D0)
                    else -> Color(0xFF9E9E9E)
                }
            )
            .clickable(enabled = !cell.isRevealed) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            cell.isFlagged -> {
                Icon(
                    Icons.Default.Flag,
                    contentDescription = "Bandera",
                    tint = Color.Red
                )
            }
            cell.isRevealed && cell.hasMine -> {
                Text(
                    text = "💣",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
            cell.isRevealed && cell.adjacentMines > 0 -> {
                Text(
                    text = cell.adjacentMines.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (cell.adjacentMines) {
                        1 -> Color.Blue
                        2 -> Color.Green
                        3 -> Color.Red
                        else -> Color.DarkGray
                    }
                )
            }
        }
    }
}

@Composable
fun DifficultyDialog(
    onDifficultySelected: (GameDifficulty) -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Selecciona Dificultad") },
        text = {
            Column {
                Button(
                    onClick = { onDifficultySelected(GameDifficulty.EASY) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fácil (8x8)")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onDifficultySelected(GameDifficulty.HARD) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Difícil (15x15)")
                }
            }
        },
        confirmButton = { }
    )
}

@Composable
fun SaveGameDialog(
    context: Context,
    onFormatSelected: (SaveFormat) -> Unit,
    onDismiss: () -> Unit
) {
    val currentFormat = StorageUtils.getPreferredFormat(context)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Guardar Partida") },
        text = {
            Column {
                Text("Selecciona el formato para guardar:")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onFormatSelected(SaveFormat.XML) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentFormat == SaveFormat.XML)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("XML")
                    }

                    Button(
                        onClick = { onFormatSelected(SaveFormat.JSON) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentFormat == SaveFormat.JSON)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("JSON")
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun GameOverDialog(
    gameState: GameState?,
    onNewGame: () -> Unit,
    onSaveGame: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (gameState?.status == GameStatus.WON) "¡Victoria!" else "¡Derrota!",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (gameState?.status == GameStatus.WON)
                        "¡Felicidades! Has completado el nivel."
                    else
                        "Has encontrado una mina. ¡Juego terminado!",
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Puntuación: ${gameState?.score ?: 0}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(onClick = onNewGame) {
                Text("Nueva Partida")
            }
        },
        dismissButton = {
            Button(onClick = onSaveGame) {
                Text("Guardar Partida")
            }
        }
    )
}