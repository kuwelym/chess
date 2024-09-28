package com.example.chess

import com.example.chess.board.Board
import com.example.chess.board.Position

sealed class Piece {
    // The player who owns the piece
    abstract val player: Player

    // Name of the piece
    abstract val name: String

    // Current position of the piece on the board
    abstract val position: Position

    // A list to store the moves of the piece
    abstract val history: List<Position>

    val isWhite: Boolean
        get() = player == Player.WHITE

    open val moves: Set<Direction> = emptySet()
    override fun toString(): String {
        return name
    }

//    val lastGeneratedMoves = mutableSetOf<Move>()

    fun generateMoves(board: Board, validateForCheck: Boolean = true) : Set<Move> {
        val generatedMoves =
        moves.flatMap { MovesGenerator.generate(board, this@Piece, validateForCheck) }
            .toSet()

//        lastGeneratedMoves.clear()
//        lastGeneratedMoves.addAll(generatedMoves)

        return generatedMoves
    }

}

val Piece.imageResource: Int
    get() = R.drawable::class.java.getField(this.name.lowercase()).getInt(null)

infix fun Piece.moveTo(position: Position): Piece {
    return when (this) {
        is Pawn -> copy(position = position, history = history + position)
        is Rook -> copy(position = position, history = history + position)
        is Knight -> copy(position = position, history = history + position)
        is Bishop -> copy(position = position, history = history + position)
        is Queen -> copy(position = position, history = history + position)
        is King -> copy(position = position, history = history + position)
    }
}

data class Pawn(
    override val player: Player,
    override val position: Position,
    override val history: List<Position> = emptyList()
) : Piece() {
    override val name: String
        get() = "Pawn_${player.name}"

    override val moves: Set<Direction>
        get() = if (player == Player.WHITE) setOf(0 to 1) else setOf(0 to -1)

    val rowDirection: Int
        get() = if (player == Player.WHITE) 1 else -1
}

data class Rook(
    override val player: Player,
    override val position: Position,
    override val history: List<Position> = emptyList()
) : Piece() {
    override val name: String
        get() = "Rook_${player.name}"

    override val moves: Set<Direction> = setOf(
        0 to 1, // right
        0 to -1, // left
        1 to 0, // up
        -1 to 0 // down
    )
}

data class Knight(
    override val player: Player,
    override val position: Position,
    override val history: List<Position> = emptyList()
) : Piece() {
    override val name: String
        get() = "Knight_${player.name}"

    override val moves: Set<Direction> = setOf(
        1 to 2, // up right
        1 to -2, // down right
        -1 to 2, // up left
        -1 to -2, // down left
        2 to 1, // right up
        2 to -1, // right down
        -2 to 1, // left up
        -2 to -1 // left down
    )
}

data class Bishop(
    override val player: Player,
    override val position: Position,
    override val history: List<Position> = emptyList()
) : Piece() {
    override val name: String
        get() = "Bishop_${player.name}"

    override val moves: Set<Direction> = setOf(
        1 to 1, // up right
        1 to -1, // down right
        -1 to 1, // up left
        -1 to -1 // down left
    )
}

data class Queen(
    override val player: Player,
    override val position: Position,
    override val history: List<Position> = emptyList()
) : Piece() {
    override val name: String
        get() = "Queen_${player.name}"

    override val moves: Set<Direction> = setOf(
        0 to 1, // right
        0 to -1, // left
        1 to 0, // up
        -1 to 0, // down
        1 to 1, // up right
        1 to -1, // down right
        -1 to 1, // up left
        -1 to -1 // down left
    )
}

data class King(
    override val player: Player,
    override val position: Position,
    override val history: List<Position> = emptyList()
) : Piece() {
    override val name: String
        get() = "King_${player.name}"

    override val moves: Set<Direction> = setOf(
        0 to 1, // right
        0 to -1, // left
        1 to 0, // up
        -1 to 0, // down
        1 to 1, // up right
        1 to -1, // down right
        -1 to 1, // up left
        -1 to -1 // down left
    )
}

typealias Direction = Pair<Int, Int>

operator fun Direction.times(value: Int): Direction {
    return Direction(first * value, second * value)
}