package com.example.chess

import com.example.chess.board.Board
import com.example.chess.board.Position
import com.example.chess.board.isOccupiedBy
import com.example.chess.board.minus
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
        }.filter { !validateForCheck || !board.simulateMove(it).isCheck() }.toSet()

    private fun pawnMoves(board: Board, piece: Pawn): Set<Move> {
        val direction = piece.rowDirection
        val moves = mutableSetOf<Move>()

        val forwardOne = piece.position + (direction to 0)
        if (board.getSquareOrNull(forwardOne)?.isEmpty == true) {
            moves += BasicMove(piece, forwardOne)
        }

        val forwardTwo = piece.position + (direction * 2 to 0)
        if (piece.history.isEmpty() && board.getSquareOrNull(forwardTwo)?.isEmpty == true && board.getSquareOrNull(
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
        val lastMoveDistance = (lastMove.piece.position.row - lastMove.dest.row) * direction
        if (lastMoveDistance != 2) return moves

        val left = piece.position + (0 to -1)
        val right = piece.position + (0 to 1)
        if (lastMove.dest == left || lastMove.dest == right) {
            moves += EnPassantMove(piece, lastMove.dest + (direction to 0), lastMove.dest)
        }

        return moves
    }

    private fun castlingMoves(board: Board, piece: King): Set<Move> {
        val moves = mutableSetOf<Move>()

        if (piece.history.isNotEmpty()) return moves

        val kingSideRook = board.getSquare(Position(piece.position.row, 7)).piece as? Rook
        val queenSideRook = board.getSquare(Position(piece.position.row, 0)).piece as? Rook

        fun isCastlingPathClear(board: Board, kingPosition: Position, direction: Int, steps: Int): Boolean {

            return (1..steps).all { offset ->
                val position = kingPosition + (0 to offset * direction)
                board.getSquareOrNull(position)?.isEmpty == true
            }
        }

        if (kingSideRook != null && kingSideRook.history.isEmpty()) {
            val kingSideEmpty =
                isCastlingPathClear(board, piece.position, 1, 2)

            if (kingSideEmpty) {
                moves += CastlingMove(
                    piece,
                    piece.position + (0 to 2),
                    kingSideRook,
                    piece.position + (0 to 1),
                    false
                )
            }
        }

        if (queenSideRook != null && queenSideRook.history.isEmpty()) {
            val queenSideEmpty =
                isCastlingPathClear(board, piece.position, -1, 3)

            if (queenSideEmpty) {
                moves += CastlingMove(
                    piece,
                    piece.position - (0 to 2),
                    queenSideRook,
                    piece.position - (0 to 1),
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