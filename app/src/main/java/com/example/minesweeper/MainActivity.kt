package com.example.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.minesweeper.ui.screens.GameScreen
import com.example.minesweeper.ui.screens.MainMenuScreen
import com.example.minesweeper.ui.screens.OptionsScreen
import com.example.minesweeper.ui.screens.SavedGamesScreen
import com.example.minesweeper.ui.theme.MinesweeperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinesweeperTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MinesweeperApp()
                }
            }
        }
    }
}

@Composable
fun MinesweeperApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") {
            MainMenuScreen(
                onNewGameClick = { navController.navigate("game/new") },
                onLoadGameClick = { navController.navigate("saved_games") },
                onOptionsClick = { navController.navigate("options") }
            )
        }

        composable("game/new") {
            GameScreen(
                gameId = null,
                onBackClick = { navController.popBackStack() },
                onGameSaved = { navController.popBackStack() }
            )
        }

        composable("game/load/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            GameScreen(
                gameId = gameId,
                onBackClick = { navController.popBackStack() },
                onGameSaved = { navController.popBackStack() }
            )
        }

        composable("saved_games") {
            SavedGamesScreen(
                onBackClick = { navController.popBackStack() },
                onGameSelected = { gameId ->
                    navController.navigate("game/load/$gameId")
                }
            )
        }

        composable("options") {
            OptionsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}