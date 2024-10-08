package com.example.chess.board


import com.example.chess.model.Bishop
import com.example.chess.game.DrawType
import com.example.chess.game.GameResult
import com.example.chess.model.King
import com.example.chess.model.Knight
import com.example.chess.model.Move
import com.example.chess.model.Pawn
import com.example.chess.model.Piece
import com.example.chess.model.Player
import com.example.chess.model.Queen
import com.example.chess.model.Rook
import com.example.chess.game.WinType
import com.example.chess.model.getImpactedSquares
import com.example.chess.game.isCheckmate
import com.example.chess.game.isStalemate
import com.example.chess.model.opponent
import com.example.chess.AppData
import com.example.chess.ui.manager.ModelViewRegistry
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

    val bitBoard = BitBoard()

    /**
     * Initializes a new board with pieces on their initial positions and the white player
     * on turn if [initializeWithPieces] is true, or an empty board without pieces
     */
    private constructor(initializeWithPieces: Boolean = true) {
        this.squares = Matrix(8, 8) { row, col ->
            val position = Position(row, col)
            val square = Square(
                position,
                if (initializeWithPieces) createPieceAtStartingPosition(position) else null
            )
            square.piece?.let { piece ->
                bitBoard.setPiece(piece, position)
            }
            square
        }
        this.currentPlayer = Player.WHITE
        this.previousBoard = null
        this.playedMoves = emptyList()
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
            val square = updatedSquares[position] ?: previousBoard.getSquare(position)
            square.piece?.let { piece ->
                bitBoard.setPiece(piece, position)
            }
            square
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
    fun playMove(move: Move, takeTurns: Boolean = true): Board {
        val updatedSquares = move.getImpactedSquares()
        val board = Board(
            previousBoard = this,
            updatedSquares = updatedSquares.associateBy { it.position },
            takeTurns = takeTurns,
            playedMoves = playedMoves.plus(move)
        )
        if (takeTurns) {
            AppData.notationViewModel.onMovePlayed(updatedSquares, board)
            ModelViewRegistry.moveSquareViewMapper.onMovePlayed(updatedSquares, board)
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
    private fun <T : Piece> getPiecesOfType(
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
    fun getKing(): Piece = getPiecesOfType(currentPlayer, King::class).first()

    /**
     * Returns a [GameResult] describing the current state of this board
     */
    fun getGameResult(): GameResult = runBlocking {
        when {

            isStalemate() -> GameResult.Draw(DrawType.STALEMATE)
            isCheckmate() && isWhiteTurn() -> GameResult.BlackWins(WinType.CHECKMATE)
            isCheckmate() && !isWhiteTurn() -> GameResult.WhiteWins(WinType.CHECKMATE)
            else -> GameResult.StillPlaying
        }
    }

    /**
     * Initializes and returns correct piece based on given [position]
     */
    private fun createPieceAtStartingPosition(position: Position): Piece? {
        val player: Player = if (position.row in 0..1) Player.WHITE else Player.BLACK
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
        fun emptyBoard() = Board(initializeWithPieces = false)

        /**
         * Returns a new chess board with pieces in initial positions
         */
        fun initialBoard() = Board()
    }
}

class BitBoard {
    var whitePieces: ULong = 0UL
    var blackPieces: ULong = 0UL
    val pieces: MutableMap<KClass<out Piece>, ULong> = mutableMapOf(
        King::class to 0UL,
        Rook::class to 0UL,
        Bishop::class to 0UL,
        Queen::class to 0UL,
        Knight::class to 0UL,
        Pawn::class to 0UL
    )

    fun setPiece(piece: Piece, position: Position) {
        val bit = position.toBit()
        pieces[piece::class] = pieces[piece::class]!! or bit
        if (piece.player == Player.WHITE) {
            whitePieces = whitePieces or bit
        } else {
            blackPieces = blackPieces or bit
        }
    }

    fun removePiece(piece: Piece, position: Position) {
        val bit = position.toBit()
        pieces[piece::class] = pieces[piece::class]!! xor bit

        if (piece.player == Player.WHITE) {
            whitePieces = whitePieces xor bit
        } else {
            blackPieces = blackPieces xor bit
        }
    }

    fun isOccupied(position: Position): Boolean {
        val bit = position.toBit()
        return (whitePieces or blackPieces) and bit != 0UL
    }
}

fun ULong.toBitString(): String {
    return this.toString(2).padStart(64, '0')
}

fun Position.toBitString(): String {
    return this.toBit().toBitString()
}

/**
 * Returns the bit representation of the given [Position]
 * The bitboard is represented as a 64-bit unsigned integer where each bit represents a square on the board
 * The least significant bit represents the square a1, the most significant bit represents h8
 */
fun Position.toBit(): ULong = 1UL shl (row * 8 + col)

/**
 * Returns the [Position] represented by the given 64-bit unsigned integer
 */
fun ULong.toPosition(): Position {
    // get index from right to left
    val index = this.toString(2).reversed().indexOf('1')
    val row = index / 8
    val col = index % 8
    return Position(row, col)
}

/**
 * Returns true if the white player is on turn.
 * Convenience method to avoid the long and ugly equality checks everytime.
 */
fun Board.isWhiteTurn(): Boolean = currentPlayer == Player.WHITE

/**
 * Simulates the given [move] and returns an updated board. Contrary to the [Board.playMove] method,
 * the players do not take turns - the move is only simulated to obtain the board state if
 * the move was played
 */
fun Board.simulateMove(move: Move): Board = playMove(move, takeTurns = false)
