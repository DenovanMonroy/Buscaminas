package com.example.minesweeper.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.minesweeper.data.GameStatus
import com.example.minesweeper.data.SaveFormat
import com.example.minesweeper.data.SavedGame
import com.example.minesweeper.persistence.GameRepository
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGamesScreen(
    onBackClick: () -> Unit,
    onGameSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val gameRepository = remember { GameRepository(context) }

    var savedGames by remember { mutableStateOf<List<SavedGame>>(emptyList()) }
    var selectedGame by remember { mutableStateOf<SavedGame?>(null) }
    var deleteConfirmationVisible by remember { mutableStateOf(false) }
    var exportDialogVisible by remember { mutableStateOf(false) }

    // Load saved games
    LaunchedEffect(Unit) {
        savedGames = gameRepository.getAllSavedGames()
    }

    // Delete game function
    fun deleteGame(game: SavedGame) {
        coroutineScope.launch {
            gameRepository.deleteGame(game)
            // Refresh list
            savedGames = gameRepository.getAllSavedGames()
            deleteConfirmationVisible = false
        }
    }

    // Export game function
    fun exportGame(game: SavedGame) {
        coroutineScope.launch {
            try {
                val extension = when (game.format) {
                    SaveFormat.XML -> ".xml"
                    SaveFormat.JSON -> ".json"
                }

                val filename = "minesweeper_save_${game.id}$extension"
                val exportFile = File(context.cacheDir, filename)

                val success = gameRepository.exportGame(game, exportFile)

                if (success) {
                    // Create an intent to share the file
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        exportFile
                    )

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = when (game.format) {
                            SaveFormat.XML -> "application/xml"
                            SaveFormat.JSON -> "application/json"
                        }
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(Intent.createChooser(shareIntent, "Compartir partida guardada"))
                }

                exportDialogVisible = false

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partidas Guardadas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (savedGames.isEmpty()) {
                EmptySavedGamesPlaceholder()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedGames) { game ->
                        SavedGameItem(
                            game = game,
                            onClick = { onGameSelected(game.id) },
                            onDeleteClick = {
                                selectedGame = game
                                deleteConfirmationVisible = true
                            },
                            onExportClick = {
                                selectedGame = game
                                exportDialogVisible = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Delete confirmation dialog
            if (deleteConfirmationVisible && selectedGame != null) {
                AlertDialog(
                    onDismissRequest = { deleteConfirmationVisible = false },
                    title = { Text("Eliminar Partida") },
                    text = { Text("¿Estás seguro de que deseas eliminar esta partida guardada?") },
                    confirmButton = {
                        Button(onClick = { selectedGame?.let { deleteGame(it) } }) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { deleteConfirmationVisible = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Export dialog
            if (exportDialogVisible && selectedGame != null) {
                AlertDialog(
                    onDismissRequest = { exportDialogVisible = false },
                    title = { Text("Exportar Partida") },
                    text = { Text("¿Deseas exportar esta partida guardada?") },
                    confirmButton = {
                        Button(onClick = { selectedGame?.let { exportGame(it) } }) {
                            Text("Exportar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { exportDialogVisible = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SavedGameItem(
    game: SavedGame,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExportClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(game.savedAt)
    val formattedTime = String.format(
        "%02d:%02d",
        game.timeElapsed / (1000 * 60),
        (game.timeElapsed / 1000) % 60
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.difficulty.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    when (game.status) {
                                        GameStatus.ONGOING -> MaterialTheme.colorScheme.primary
                                        GameStatus.WON -> MaterialTheme.colorScheme.tertiary
                                        GameStatus.LOST -> MaterialTheme.colorScheme.error
                                    }
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (game.status) {
                                    GameStatus.ONGOING -> "En progreso"
                                    GameStatus.WON -> "Completado"
                                    GameStatus.LOST -> "Perdido"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Puntuación: ${game.score}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Tiempo: $formattedTime",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Formato: ${game.format.name}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row {
                    // Export button
                    IconButton(onClick = onExportClick) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Exportar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Delete button
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}