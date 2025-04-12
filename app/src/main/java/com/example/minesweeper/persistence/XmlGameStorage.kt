package com.example.minesweeper.persistence

import android.content.Context
import com.example.minesweeper.data.Cell
import com.example.minesweeper.data.GameDifficulty
import com.example.minesweeper.data.GameState
import com.example.minesweeper.data.GameStatus
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class XmlGameStorage(private val context: Context) {

    private val gamesDir = File(context.filesDir, "games/xml")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    init {
        if (!gamesDir.exists()) {
            gamesDir.mkdirs()
        }
    }

    suspend fun saveGame(gameState: GameState): Boolean {
        try {
            val file = File(gamesDir, "${gameState.id}.xml")
            FileOutputStream(file).use { fos ->
                val serializer = XmlPullParserFactory.newInstance().newSerializer()
                serializer.setOutput(fos, "UTF-8")
                serializer.startDocument("UTF-8", true)

                // Root element
                serializer.startTag("", "gameState")

                // Game state attributes
                serializer.attribute("", "id", gameState.id)
                serializer.attribute("", "difficulty", gameState.difficulty.name)
                serializer.attribute("", "status", gameState.status.name)
                serializer.attribute("", "score", gameState.score.toString())
                serializer.attribute("", "timeElapsed", gameState.timeElapsed.toString())
                gameState.startTime?.let {
                    serializer.attribute("", "startTime", dateFormat.format(it))
                }

                // Board
                serializer.startTag("", "board")
                serializer.attribute("", "size", gameState.difficulty.boardSize.toString())

                gameState.board.forEach { row ->
                    row.forEach { cell ->
                        serializer.startTag("", "cell")
                        serializer.attribute("", "x", cell.x.toString())
                        serializer.attribute("", "y", cell.y.toString())
                        serializer.attribute("", "hasMine", cell.hasMine.toString())
                        serializer.attribute("", "isRevealed", cell.isRevealed.toString())
                        serializer.attribute("", "isFlagged", cell.isFlagged.toString())
                        serializer.attribute("", "adjacentMines", cell.adjacentMines.toString())
                        serializer.endTag("", "cell")
                    }
                }

                serializer.endTag("", "board")
                serializer.endTag("", "gameState")
                serializer.endDocument()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun loadGame(gameId: String): GameState? {
        try {
            val file = File(gamesDir, "$gameId.xml")
            if (!file.exists()) return null

            FileInputStream(file).use { fis ->
                val parser = XmlPullParserFactory.newInstance().newPullParser()
                parser.setInput(fis, null)

                var eventType = parser.eventType

                var id: String? = null
                var difficultyStr: String? = null
                var statusStr: String? = null
                var score: Int = 0
                var timeElapsed: Long = 0
                var startTimeStr: String? = null
                var boardSize: Int = 0
                val cellList = mutableListOf<Cell>()

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (parser.name) {
                                "gameState" -> {
                                    id = parser.getAttributeValue(null, "id")
                                    difficultyStr = parser.getAttributeValue(null, "difficulty")
                                    statusStr = parser.getAttributeValue(null, "status")
                                    score = parser.getAttributeValue(null, "score").toInt()
                                    timeElapsed = parser.getAttributeValue(null, "timeElapsed").toLong()
                                    startTimeStr = parser.getAttributeValue(null, "startTime")
                                }
                                "board" -> {
                                    boardSize = parser.getAttributeValue(null, "size").toInt()
                                }
                                "cell" -> {
                                    val x = parser.getAttributeValue(null, "x").toInt()
                                    val y = parser.getAttributeValue(null, "y").toInt()
                                    val hasMine = parser.getAttributeValue(null, "hasMine").toBoolean()
                                    val isRevealed = parser.getAttributeValue(null, "isRevealed").toBoolean()
                                    val isFlagged = parser.getAttributeValue(null, "isFlagged").toBoolean()
                                    val adjacentMines = parser.getAttributeValue(null, "adjacentMines").toInt()

                                    cellList.add(
                                        Cell(
                                            x = x,
                                            y = y,
                                            hasMine = hasMine,
                                            isRevealed = isRevealed,
                                            isFlagged = isFlagged,
                                            adjacentMines = adjacentMines
                                        )
                                    )
                                }
                            }
                        }
                    }
                    eventType = parser.next()
                }

                // Create board from cell list
                val board = List(boardSize) { x ->
                    List(boardSize) { y ->
                        cellList.find { it.x == x && it.y == y } ?:
                        Cell(x, y) // Default cell if not found
                    }
                }

                return GameState(
                    id = id ?: gameId,
                    difficulty = GameDifficulty.valueOf(difficultyStr ?: GameDifficulty.EASY.name),
                    status = GameStatus.valueOf(statusStr ?: GameStatus.ONGOING.name),
                    board = board,
                    score = score,
                    timeElapsed = timeElapsed,
                    startTime = if (startTimeStr != null) dateFormat.parse(startTimeStr) else null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getGameFile(gameId: String): File? {
        val file = File(gamesDir, "$gameId.xml")
        return if (file.exists()) file else null
    }

    suspend fun deleteGame(gameId: String): Boolean {
        val file = File(gamesDir, "$gameId.xml")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}