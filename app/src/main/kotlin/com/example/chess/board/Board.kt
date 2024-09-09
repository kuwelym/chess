package com.example.chess.board


import android.util.Log
import androidx.core.view.children
import androidx.lifecycle.ViewModel
import com.example.chess.Bishop
import com.example.chess.DrawType
import com.example.chess.GameResult
import com.example.chess.King
import com.example.chess.Knight
import com.example.chess.MainActivity
import com.example.chess.Move
import com.example.chess.Pawn
import com.example.chess.Piece
import com.example.chess.Player
import com.example.chess.Queen
import com.example.chess.Rook
import com.example.chess.WinType
import com.example.chess.getImpactedSquares
import com.example.chess.isCheck
import com.example.chess.isCheckmate
import com.example.chess.isStalemate
import com.example.chess.opponent
import com.example.chess.ui.AppData
import com.example.chess.ui.ChessPieceView
import com.example.chess.ui.DefaultModelViewMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Chess board with a 8x8 matrix of [Square]s holding the current game state.
 */
class Board {

    /**
     * A 8x8 matrix of squares
     */
    val squares: Matrix<Square>

    /**
     * The player who is on the turn
     */
    val currentPlayer: Player

    /**
     * The previous state of the game (enables the "undo" feature)
     */
    val previousBoard: Board?

    /**
     * History of played moves
     */
    val playedMoves: List<Move>

    /**
     * Initializes a new board with pieces on their initial positions and the white player
     * on turn if [setPieces] is true, or an empty board without pieces
     */
    private constructor(setPieces: Boolean = true) {
        this.squares = Matrix(8, 8) { row, col ->
            Position(row, col).let {
                Square(it, if (setPieces) resolvePiece(it) else null)
            }
        }
        this.currentPlayer = Player.WHITE
        this.previousBoard = null
        this.playedMoves = emptyList()

        CoroutineScope(Dispatchers.IO).launch {
            generateMovesForBoard(this@Board)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun generateMovesForBoard(board: Board) {
        val dispatcher = Dispatchers.IO.limitedParallelism(Runtime.getRuntime().availableProcessors())
        coroutineScope {
            getPieces(board.currentPlayer).forEach { piece ->
                launch(dispatcher) {
                    try {
                        piece.generateMoves(board)
                    } catch (e: Exception) {
                        Log.e("Board", "Error generating moves for piece $piece", e)
                    }
                }
            }
        }
    }

    /**
     * Initializes a new board as a result of updating the [previousBoard] with
     * given [updatedSquares]. If [takeTurns] is true, the players take turns.
     */
    private constructor (
        previousBoard: Board,
        updatedSquares: Map<Position, Square>,
        takeTurns: Boolean,
        playedMoves: List<Move>
    ) {
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            updatedSquares[position] ?: previousBoard.getSquare(position)
        }
        this.currentPlayer =
            if (takeTurns) previousBoard.currentPlayer.opponent() else previousBoard.currentPlayer
        this.previousBoard = previousBoard
        this.playedMoves = playedMoves

    }

    /**
     * Plays the given [move] and returns an updated board with the move recorded
     * in the list of played moves. If [takeTurns] is true, the players take turn.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun playMove(move: Move, takeTurns: Boolean = true): Board {
        val updatedSquares = move.getImpactedSquares()
        val board = Board(
            previousBoard = this,
            updatedSquares = updatedSquares.associateBy { it.position },
            takeTurns = takeTurns,
            playedMoves = playedMoves.plus(move)
        )
        if (takeTurns) {
            board.squares.forEach{
                Log.d("Board", "Square: ${it.piece}")
            }
            DefaultModelViewMapper.pieceViewMapper.onMovePlayed(updatedSquares, board)
            val dispatcher = Dispatchers.IO.limitedParallelism(Runtime.getRuntime().availableProcessors())
            CoroutineScope(dispatcher).launch {
                generateMovesForBoard(board)
            }
            AppData.notationViewModel.updatePlayedNotations(board.playedMoves)
        }
        return board
    }

    /**
     * Returns the [Square] on given [position], or throws exception if given position is not on the board
     */
    fun getSquare(position: Position): Square = if (position.isValid) {
        squares[position]
    } else {
        throw IllegalArgumentException("Position $position not on board")
    }

    /**
     * Returns the [Square] on given [position], or null if the given position is not on the board
     */
    fun getSquareOrNull(position: Position): Square? =
        if (position.isValid) squares[position] else null

    /**
     * Returns all pieces of given [player] and [type]
     */
    private fun <T : Piece> getPieces(
        player: Player = currentPlayer,
        type: KClass<T>
    ): List<T> =
        squares
            .mapNotNull { it.piece }
            .filter { it.player == player }
            .filterIsInstance(type.java)
            .map { type.cast(it) }

    /**
     * Returns all pieces of given [player]
     */
    fun getPieces(player: Player = currentPlayer): List<Piece> = squares
        .mapNotNull { it.piece }
        .filter { it.player == player }

    /**
     * Returns the king of the player on turn
     */
    fun getKing(): Piece = getPieces(currentPlayer, King::class).first()

    /**
     * Returns a [GameResult] describing the current state of this board
     */
    fun getGameResult(): GameResult = runBlocking {
        when {

            isStalemate() -> GameResult.Draw(DrawType.STALEMATE)
            isCheckmate() && whiteOnTurn() -> GameResult.BlackWins(WinType.CHECKMATE)
            isCheckmate() && !whiteOnTurn() -> GameResult.WhiteWins(WinType.CHECKMATE)
            else -> GameResult.StillPlaying
        }
    }

    /**
     * Initializes and returns correct piece based on given [position]
     */
    private fun resolvePiece(position: Position): Piece? {
        val player: Player = if (position.row in 0..1) Player.BLACK else Player.WHITE
        return PieceFactory.createPiece(position, player)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (squares != other.squares) return false
        if (currentPlayer != other.currentPlayer) return false
        if (previousBoard != other.previousBoard) return false
        if (playedMoves != other.playedMoves) return false

        return true
    }

    override fun hashCode(): Int {
        var result = squares.hashCode()
        result = 31 * result + currentPlayer.hashCode()
        result = 31 * result + (previousBoard?.hashCode() ?: 0)
        result = 31 * result + playedMoves.hashCode()
        return result
    }

    companion object {

        /**
         * Returns a new empty chess board without pieces
         */
        fun emptyBoard() = Board(setPieces = false)

        /**
         * Returns a new chess board with pieces in initial positions
         */
        fun initialBoard() = Board()
    }
}

/**
 * Returns true if the white player is on turn.
 * Convenience method to avoid the long and ugly equality checks everytime.
 */
fun Board.whiteOnTurn(): Boolean = currentPlayer == Player.WHITE

/**
 * Simulates the given [move] and returns an updated board. Contrary to the [Board.playMove] method,
 * the players do not take turns - the move is only simulated to obtain the board state if
 * the move was played
 */
fun Board.simulateMove(move: Move): Board = playMove(move, false)