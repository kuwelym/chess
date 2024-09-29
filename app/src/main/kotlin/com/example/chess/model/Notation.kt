package com.example.chess.model

import com.example.chess.board.Board
import com.example.chess.game.isCheck

// Algebraic notation is a standard way to identify each square on the chessboard.
enum class PieceNotationType{
    TEXT,
    FIGURINE
}

// symbol is text or figurine representation of a piece
val Piece?.symbol: String
    get() = when (this) {
        is Pawn -> ""
        is Rook -> "R"
        is Knight -> "N"
        is Bishop -> "B"
        is Queen -> "Q"
        is King -> "K"
        else -> ""
    }

fun Move.checkSymbol(board: Board) = if (board.isCheck()) "+" else ""