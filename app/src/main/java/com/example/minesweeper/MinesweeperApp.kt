package com.example.minesweeper

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.minesweeper.ui.screens.GameScreen
import com.example.minesweeper.ui.screens.MainMenuScreen
import com.example.minesweeper.ui.screens.OptionsScreen
import com.example.minesweeper.ui.screens.SavedGamesScreen

@Composable
fun MinesweeperApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main_menu"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
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

        composable(
            "game/load/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
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