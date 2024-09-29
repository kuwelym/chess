package com.example.chess

import com.example.chess.board.BitBoard
import com.example.chess.board.Board
import com.example.chess.board.Position
import com.example.chess.board.plus
import com.example.chess.board.simulateMove
import com.example.chess.board.toBit
import com.example.chess.board.toPosition
import com.example.chess.ui.AppData

object MovesGenerator {

    private const val PAWN_FORWARD_ONE = 8
    private const val PAWN_ATTACK_LEFT = 7
    private const val PAWN_ATTACK_RIGHT = 9
    private const val KING_CASTLING_KING_SIDE_STEPS = 2
    private const val KING_CASTLING_QUEEN_SIDE_STEPS = 3

    fun generate(board: Board, piece: Piece, validateForCheck: Boolean): Set<Move> =
        when (piece) {
            is Pawn -> board.bitBoard.pawnMoves(piece).union(enPassantMoves(piece))
            is Knight -> board.bitBoard.generateMoves(piece, 1)
            is Bishop -> board.bitBoard.generateMoves(piece, 7)
            is Rook -> board.bitBoard.generateMoves(piece, 7)
            is Queen -> board.bitBoard.generateMoves(piece, 7)
            is King -> board.bitBoard.generateMoves(piece, 1)
                .union(board.bitBoard.castlingMoves(piece))
        }.filter { !validateForCheck || !board.simulateMove(it).isCheckBitboard() }.toSet()

    private fun BitBoard.pawnMoves(piece: Pawn): Set<Move> {
        val moves = mutableSetOf<Move>()
        val position = piece.position
        val bit = position.toBit()
        val direction = piece.rowDirection

        val forwardOne = if (direction > 0) bit shl PAWN_FORWARD_ONE else bit shr PAWN_FORWARD_ONE
        if (forwardOne and (whitePieces or blackPieces) == 0UL) {
            moves.add(BasicMove(piece, forwardOne.toPosition()))
        }

        val forwardTwo =
            if (direction > 0) forwardOne shl PAWN_FORWARD_ONE else forwardOne shr PAWN_FORWARD_ONE
        if (piece.history.isEmpty() && forwardTwo and (whitePieces or blackPieces) == 0UL) {
            moves.add(BasicMove(piece, forwardTwo.toPosition()))
        }

        val attacks = arrayOf(PAWN_ATTACK_LEFT, PAWN_ATTACK_RIGHT)
        for (attack in attacks) {
            val attackBit = if (direction > 0) bit shl attack else bit shr attack
            if (attackBit and (piece.player.let { if (it == Player.WHITE) blackPieces else whitePieces }) != 0UL) {
                moves.add(BasicMove(piece, attackBit.toPosition(), true))
            }
        }
        return moves
    }

    private fun enPassantMoves(piece: Pawn): Set<Move> {
        val moves = mutableSetOf<Move>()
        val lastMove = AppData.board.playedMoves.lastOrNull() as? BasicMove ?: return moves
        val lastPiece = lastMove.piece as? Pawn ?: return moves
        if (lastPiece.player == piece.player) return moves

        val direction = piece.rowDirection
        val lastMoveDistance = (lastMove.piece.position.row - lastMove.dest.row) * direction
        if (lastMoveDistance != 2) return moves

        val left = piece.position + (0 to -1)
        val right = piece.position + (0 to 1)
        if (lastMove.dest == left || lastMove.dest == right) {
            moves.add(EnPassantMove(piece, lastMove.dest + (direction to 0), lastMove.dest))
        }

        return moves
    }

    private fun BitBoard.castlingMoves(piece: King): Set<Move> {
        val moves = mutableSetOf<Move>()

        if (piece.history.isNotEmpty()) return moves

        val kingSideRook = AppData.board.getSquare(Position(piece.position.row, 7)).piece as? Rook
        val queenSideRook = AppData.board.getSquare(Position(piece.position.row, 0)).piece as? Rook

        fun isCastlingPathClear(
            bitBoard: BitBoard,
            kingPosition: Position,
            direction: Int,
            steps: Int
        ): Boolean {
            val bit = kingPosition.toBit()
            for (i in 1..steps) {
                val newPosition = if (direction > 0) bit shl i else bit shr i
                // check if the paths are under attack
                if (bitBoard.whitePieces and newPosition != 0UL || bitBoard.blackPieces and newPosition != 0UL) {
                    return false
                }
                if (this.isAttackedByAnyPiece(newPosition.toPosition(), AppData.board)) {
                    return false
                }
            }
            return true
        }

        if (kingSideRook != null && kingSideRook.history.isEmpty() &&
            isCastlingPathClear(this, piece.position, 1, KING_CASTLING_KING_SIDE_STEPS)
        ) {
            moves.add(
                CastlingMove(
                    piece,
                    piece.position + (0 to 2),
                    kingSideRook,
                    kingSideRook.position + (0 to -2),
                    queenSide = false
                )
            )
        }

        if (queenSideRook != null && queenSideRook.history.isEmpty() &&
            isCastlingPathClear(this, piece.position, -1, KING_CASTLING_QUEEN_SIDE_STEPS)
        ) {
            moves.add(
                CastlingMove(
                    piece,
                    piece.position + (0 to -2),
                    queenSideRook,
                    queenSideRook.position + (0 to 3),
                    queenSide = true
                )
            )
        }

        return moves
    }

    private fun BitBoard.generateMoves(
        piece: Piece,
        maxDistance: Int = 7
    ): Set<Move> {
        fun generateMoveRecursively(
            direction: Direction,
            distance: Int,
            moves: Set<Move> = emptySet()
        ): Set<Move> {
            if (distance > maxDistance) return moves

            val newPosition = piece.position + (direction * distance)
            if (!newPosition.isValid) return moves

            val newBit = newPosition.toBit()
            val isOccupied = (whitePieces or blackPieces) and newBit != 0UL
            val isOccupiedByOpponent =
                if (piece.isWhite) blackPieces and newBit != 0UL else whitePieces and newBit != 0UL

            return when {
                !isOccupied -> generateMoveRecursively(
                    direction,
                    distance + 1,
                    moves + BasicMove(piece, newPosition)
                )

                isOccupiedByOpponent -> moves + BasicMove(piece, newPosition, true)
                else -> moves
            }
        }

        return piece.moves.flatMap { generateMoveRecursively(it, 1) }.toSet()
    }

}