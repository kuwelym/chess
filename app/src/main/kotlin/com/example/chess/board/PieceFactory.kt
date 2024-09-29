package com.example.chess.board

import com.example.chess.model.Bishop
import com.example.chess.model.King
import com.example.chess.model.Knight
import com.example.chess.model.Pawn
import com.example.chess.model.Piece
import com.example.chess.model.Player
import com.example.chess.model.Queen
import com.example.chess.model.Rook

object PieceFactory {
    fun createPiece(position: Position, player: Player): Piece? {
        return when (position.row) {
            1, 6 -> Pawn(player, position)
            0, 7 -> when (position.col) {
                0, 7 -> Rook(player, position)
                1, 6 -> Knight(player, position)
                2, 5 -> Bishop(player, position)
                3 -> Queen(player, position)
                4 -> King(player, position)
                else -> throw IllegalArgumentException("Position out of bounds")
            }
            else -> null
        }
    }
}