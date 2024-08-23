package com.example.chess

import android.util.Log
import com.example.chess.board.Board
import com.example.chess.board.isOccupiedBy
import com.example.chess.board.plus
import com.example.chess.board.simulateMove

object MovesGenerator {

    fun generate(board: Board, piece: Piece, validateForCheck: Boolean): Set<Move> =
        when (piece) {
        is Pawn -> pawnMoves(board, piece) + enPassantMoves(board, piece)
        is Knight -> generateMoves(board, piece, 1)
        is Bishop -> generateMoves(board, piece, 7)
        is Rook -> generateMoves(board, piece, 7)
        is Queen -> generateMoves(board, piece, 7)
        is King -> generateMoves(board, piece, 1) + castlingMoves(board, piece)
    }.filter { !validateForCheck || board.simulateMove(it).isNotCheck() }.toSet()

    private fun pawnMoves(board: Board, piece: Pawn): Set<Move> {
        val direction = piece.rowDirection
        val moves = mutableSetOf<Move>()

        val forwardOne = piece.position + (direction to 0)
        if (board.getSquareOrNull(forwardOne)?.isEmpty == true) {
            moves += BasicMove(piece, forwardOne)
        }

        val forwardTwo = piece.position + (direction * 2 to 0)
        if (piece.history.size == 1 && board.getSquareOrNull(forwardTwo)?.isEmpty == true && board.getSquareOrNull(
                forwardOne
            )?.isEmpty == true
        ) {
            moves += BasicMove(piece, forwardTwo)
        }

        val attackLeft = piece.position + (direction to -1)
        if (board.getSquareOrNull(attackLeft)?.isOccupiedBy(piece.player.opponent()) == true) {
            moves += BasicMove(piece, attackLeft, true)
        }

        val attackRight = piece.position + (direction to 1)
        if (board.getSquareOrNull(attackRight)?.isOccupiedBy(piece.player.opponent()) == true) {
            moves += BasicMove(piece, attackRight, true)
        }

        return moves
    }

    private fun enPassantMoves(board: Board, piece: Pawn): Set<Move> {
        val moves = mutableSetOf<Move>()

        val lastMove = board.playedMoves.lastOrNull() as? BasicMove ?: return moves
        val lastPiece = lastMove.piece as? Pawn ?: return moves
        if (lastPiece.player == piece.player) return moves

        val direction = piece.rowDirection
        val lastMoveDistance = lastMove.piece.position.row - lastMove.dest.row
        if (lastMoveDistance != 2) return moves

        val left = piece.position + (-1 to direction)
        val right = piece.position + (1 to direction)
        if (lastMove.dest == left || lastMove.dest == right) {
            moves += EnPassantMove(piece, lastMove.dest + (0 to -direction), lastMove.dest)
        }

        return moves
    }

    private fun castlingMoves(board: Board, piece: King): Set<Move> {
        val moves = mutableSetOf<Move>()

        if (piece.history.isNotEmpty()) return moves

        val rooks = board.getPieces(piece.player).filterIsInstance<Rook>()
        val kingSideRook = rooks.firstOrNull { it.position.col == 7 }
        val queenSideRook = rooks.firstOrNull { it.position.col == 0 }

        if (kingSideRook != null && kingSideRook.history.isEmpty()) {
            val kingSideEmpty =
                (5..6).all { board.getSquareOrNull(piece.position + (it to 0))?.isEmpty == true }
            if (kingSideEmpty) {
                moves += CastlingMove(
                    kingSideRook,
                    piece.position + (6 to 0),
                    piece,
                    piece.position + (5 to 0),
                    false
                )
            }
        }

        if (queenSideRook != null && queenSideRook.history.isEmpty()) {
            val queenSideEmpty =
                (1..3).all { board.getSquareOrNull(piece.position + (it to 0))?.isEmpty == true }
            if (queenSideEmpty) {
                moves += CastlingMove(
                    queenSideRook,
                    piece.position + (2 to 0),
                    piece,
                    piece.position + (3 to 0),
                    true
                )
            }
        }

        return moves
    }

    private fun generateMoves(board: Board, piece: Piece, maxDistance: Int = 7): Set<Move> {
        fun generateMoveRecursively(
            direction: Direction,
            distance: Int,
            moves: Set<Move> = emptySet()
        ): Set<Move> {
            if (distance > maxDistance) return moves

            val newPosition = piece.position + (direction * distance)
            val newSquare = board.getSquareOrNull(newPosition)

            return when {
                newSquare == null -> moves
                newSquare.isEmpty -> generateMoveRecursively(
                    direction,
                    distance + 1,
                    moves + BasicMove(piece, newPosition)
                )

                newSquare isOccupiedBy piece.player -> moves
                newSquare isOccupiedBy piece.player.opponent() -> moves + BasicMove(
                    piece,
                    newPosition,
                    true
                )

                else -> throw IllegalStateException("Invalid square")
            }
        }
        return piece.moves.flatMap { generateMoveRecursively(it, 1) }.toSet()
    }
}