package com.example.chess

// Algebraic notation is a standard way to identify each square on the chessboard.
enum class PieceNotationType{
    TEXT,
    FIGURINE
}

// symbol is text or figurine representation of a piece
val Piece?.symbol: String
    get() = when(this){
        is Pawn -> ""
        is Rook -> "R"
        is Knight -> "N"
        is Bishop -> "B"
        is Queen -> "Q"
        is King -> "K"
        else -> ""
    }
