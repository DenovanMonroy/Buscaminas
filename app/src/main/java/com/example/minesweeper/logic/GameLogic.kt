package com.example.minesweeper.logic

import com.example.minesweeper.data.Cell
import com.example.minesweeper.data.GameDifficulty
import com.example.minesweeper.data.GameState
import com.example.minesweeper.data.GameStatus
import com.example.minesweeper.data.ScoreSystem
import java.util.Date

class GameLogic {

    fun createNewGame(difficulty: GameDifficulty): GameState {
        val size = difficulty.boardSize
        val mineCount = difficulty.mineCount

        // Initialize empty board
        var board = List(size) { x ->
            List(size) { y ->
                Cell(x, y)
            }
        }

        // Place mines
        board = MineGenerator.placeMines(board, mineCount)

        // Calculate adjacent mines for each cell
        board = calculateAdjacentMines(board)

        return GameState(
            difficulty = difficulty,
            board = board,
            status = GameStatus.ONGOING,
            startTime = Date()
        )
    }

    // Reveal a cell on the board
    fun revealCell(gameState: GameState, x: Int, y: Int): GameState {
        if (gameState.status != GameStatus.ONGOING) return gameState

        val currentCell = gameState.board[x][y]
        if (currentCell.isRevealed || currentCell.isFlagged) return gameState

        // Clone the board for immutability
        var newBoard = gameState.board.map { it.toList() }.toList()

        // If mine is revealed, game over
        if (currentCell.hasMine) {
            newBoard = newBoard.map { row ->
                row.map { cell ->
                    if (cell.hasMine) cell.copy(isRevealed = true) else cell
                }
            }
            return gameState.copy(
                board = newBoard,
                status = GameStatus.LOST
            )
        }

        // Reveal the cell and potentially neighboring cells
        newBoard = revealCellRecursive(newBoard, x, y)

        // Check if the game is won
        val allNonMineRevealed = newBoard.flatten().all { it.hasMine || it.isRevealed }
        val newStatus = if (allNonMineRevealed) GameStatus.WON else gameState.status

        // Calculate score
        val revealedCells = newBoard.flatten().count { it.isRevealed && !it.hasMine }
        val totalNonMineCells = newBoard.flatten().count { !it.hasMine }
        val timeElapsed = Date().time - (gameState.startTime?.time ?: Date().time)

        val score = if (newStatus == GameStatus.WON) {
            ScoreSystem.calculateScore(
                timeElapsed,
                gameState.difficulty,
                revealedCells,
                totalNonMineCells
            )
        } else gameState.score

        return gameState.copy(
            board = newBoard,
            status = newStatus,
            score = score,
            timeElapsed = timeElapsed
        )
    }

    // Toggle flag on a cell
    fun toggleFlag(gameState: GameState, x: Int, y: Int): GameState {
        if (gameState.status != GameStatus.ONGOING) return gameState

        val currentCell = gameState.board[x][y]
        if (currentCell.isRevealed) return gameState

        // Create mutable copy of the board
        val mutableBoard = gameState.board.map { it.toMutableList() }.toMutableList()
        mutableBoard[x][y] = currentCell.copy(isFlagged = !currentCell.isFlagged)

        return gameState.copy(board = mutableBoard.map { it.toList() })
    }

    // Reveal cells recursively for empty cells
    private fun revealCellRecursive(
        board: List<List<Cell>>,
        x: Int,
        y: Int
    ): List<List<Cell>> {
        val size = board.size
        if (x < 0 || x >= size || y < 0 || y >= size) return board

        val cell = board[x][y]
        if (cell.isRevealed || cell.isFlagged) return board

        // Make a mutable copy of the board
        val mutableBoard = board.map { it.toMutableList() }.toMutableList()
        // Update the cell to revealed
        mutableBoard[x][y] = cell.copy(isRevealed = true)

        // If cell has no adjacent mines, reveal neighbors recursively
        if (cell.adjacentMines == 0) {
            // For each neighboring cell
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in 0 until size && ny in 0 until size) {
                        // Create a new board after each recursive revelation
                        val updatedBoard = revealCellRecursive(mutableBoard.map { it.toList() }, nx, ny)
                        // Copy the updated values back to our mutable board
                        for (i in updatedBoard.indices) {
                            for (j in updatedBoard[i].indices) {
                                mutableBoard[i][j] = updatedBoard[i][j]
                            }
                        }
                    }
                }
            }
        }

        // Convert back to immutable list and return
        return mutableBoard.map { it.toList() }
    }

    // Calculate adjacent mines for each cell
    private fun calculateAdjacentMines(board: List<List<Cell>>): List<List<Cell>> {
        val size = board.size
        return board.mapIndexed { x, row ->
            row.mapIndexed { y, cell ->
                var count = 0
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val nx = x + dx
                        val ny = y + dy
                        if (nx in 0 until size && ny in 0 until size && board[nx][ny].hasMine) {
                            count++
                        }
                    }
                }
                cell.copy(adjacentMines = count)
            }
        }
    }
}