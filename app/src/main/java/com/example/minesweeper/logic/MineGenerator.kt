package com.example.minesweeper.logic

import com.example.minesweeper.data.Cell
import kotlin.random.Random

object MineGenerator {

    fun placeMines(board: List<List<Cell>>, mineCount: Int): List<List<Cell>> {
        val size = board.size
        val mutableBoard = board.map { it.toMutableList() }.toMutableList()
        val positions = mutableSetOf<Pair<Int, Int>>()

        // Generate random mine positions
        while (positions.size < mineCount) {
            val x = Random.nextInt(size)
            val y = Random.nextInt(size)
            positions.add(x to y)
        }

        // Place mines on the board
        for ((x, y) in positions) {
            mutableBoard[x][y] = mutableBoard[x][y].copy(hasMine = true)
        }

        return mutableBoard
    }
}