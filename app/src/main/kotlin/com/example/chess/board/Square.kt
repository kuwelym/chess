package com.example.chess.board

import com.example.chess.Piece
import com.example.chess.Player

/** A square is a cell on the chessboard.
 * @param position The position of the square on the board.
 * @param piece The piece on the square, if any.
 */
class Square (val position: Position, val piece: Piece? = null) {
    val isOccupied: Boolean
        get() = piece != null

    val isEmpty: Boolean
        get() = piece == null

    init {
        require(position.isValid) { "Invalid position" }
    }
}

infix fun Square.isOccupiedBy(player: Player): Boolean {
    return piece?.player == player
}