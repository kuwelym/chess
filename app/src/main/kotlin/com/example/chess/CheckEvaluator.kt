package com.example.chess

import com.example.chess.board.BitBoard
import com.example.chess.board.Board
import com.example.chess.board.Position
import com.example.chess.board.Square
import com.example.chess.board.isWhiteTurn
import com.example.chess.board.toBit
import com.example.chess.board.toPosition

/**
 * Returns true if the given [Position] is under attack by the opponent's pieces on the given [board].
 */
fun Position.isUnderAttack(board: Board) = board.getPieces(board.currentPlayer.opponent())
    .flatMap { it.generateMoves(board = board, validateForCheck = false) }
    .filterIsInstance<BasicMove>()
    .any { it.dest == this }

/**
 * Returns true if the given [Position] is under attack by the opponent's pieces on the given [board] using bitboard operations.
 */
private fun BitBoard.isKingInCheck(board: Board): Boolean {
    val kingPosition =
        (pieces[King::class]!! and (if (board.currentPlayer == Player.WHITE) whitePieces else blackPieces)).toPosition()

    return isAttackedByAnyPiece(kingPosition, board)
}

/**
 * Returns true if the given [Position] is under attack by any piece on the given [board] using bitboard operations.
 */
fun BitBoard.isAttackedByAnyPiece(position: Position, board: Board): Boolean {
    return listOf(
        ::isAttackedByPawn,
        ::isAttackedByKnight,
        ::isAttackedByRook,
        ::isAttackedByBishop,
        ::isAttackedByQueen
    ).any { attackCheck -> attackCheck(position, board) }
}


fun isUnderAttackBitboard(board: Board): Boolean {
    val bitboard = board.bitBoard
    return bitboard.isKingInCheck(board)
}

/**
 * Returns true if the square is in check w.r.t given [board] state
 */
fun Square.isUnderAttack(board: Board) = position.isUnderAttack(board)

/**
 * Returns true if the piece is in check w.r.t given [board] state
 */
fun Piece.isUnderAttack(board: Board) = position.isUnderAttack(board)

/**
 * Returns true if the king of the player on turn is in check
 */
fun Board.isCheck() = getKing().position.isUnderAttack(this@isCheck)

/**
 * Returns true if the king of the player on turn is in check, using bitboard operations
 */
fun Board.isCheckBitboard() = isUnderAttackBitboard(this)

/**
 * Returns true if the king of the player on turn has been checkmated.
 * The king is in check and there are no legal moves for the current player.
 * This function uses bitboard operations.
 */
fun Board.isCheckmateBitboard() = isCheckBitboard() && getPieces(currentPlayer).all { it.generateMoves(this).isEmpty() }

/**
 * Returns true if the king of the player on turn is in check
 */
fun King.isCheck(board: Board) = position.isUnderAttack(board)

/**
 * Returns true if the king of the player on turn is not in check
 */
fun Board.isNotCheck() = !isCheck()

/**
 * Returns true if the king of the player on turn has been checkmated.
 * The king is in check and there are no legal moves for the current player.
 */
fun Board.isCheckmate() =
    isCheck() && getPieces(currentPlayer).all { it.generateMoves(this).isEmpty() }

/**
 * Returns true if a stalemate occurred.
 * The king is not in check, but there are no legal moves for the current player.
 */
fun Board.isStalemate() =
    !isCheck() && getPieces(currentPlayer).all { it.generateMoves(this).isEmpty() }

/**
 * Returns true if the given [Position] is under attack by a pawn on the given [board] using bitboard operations.
 */
fun BitBoard.isAttackedByPawn(position: Position, board: Board): Boolean {
    val bit = position.toBit()
    val direction = if (board.isWhiteTurn()) 1 else -1
    val leftAttack = bit shr (8 + direction)
    val rightAttack = bit shr (8 - direction)
    val opponentPawns =
        if (board.isWhiteTurn()) blackPieces and pieces[Pawn::class]!! else whitePieces and pieces[Pawn::class]!!
    return (leftAttack and opponentPawns != 0UL) || (rightAttack and opponentPawns != 0UL)
}

/**
 * Returns true if the given [Position] is under attack by a knight on the given [board] using bitboard operations.
 */
fun BitBoard.isAttackedByKnight(position: Position, board: Board): Boolean {
    val bit = position.toBit()
    val knightMoves = listOf(
        6, 10, 15, 17, -6, -10, -15, -17
    )
    val opponentKnights =
        if (board.isWhiteTurn()) blackPieces and pieces[Knight::class]!! else whitePieces and pieces[Knight::class]!!
    return knightMoves.any {

        val newPosition = if (it > 0) bit shl it else bit shr -it
        if (!newPosition.toPosition().isValid) return@any false
        opponentKnights and newPosition != 0UL
    }
}

/**
 * Returns true if the given [Position] is under attack by orthogonal or diagonal moving-pieces on the given [BitBoard] using bitboard operations.
 */
private fun BitBoard.isAttackedInDirections(
    position: Position,
    directions: List<Int>,
    opponentPieces: ULong
): Boolean {
    val bit = position.toBit()
    for (direction in directions) {
        var currentBit = bit
        while (true) {
            currentBit = if (direction > 0) currentBit shl direction else currentBit shr -direction
            if (currentBit == 0UL) break
            val newPosition = currentBit.toPosition()
            if (!newPosition.isValid) break
            if (opponentPieces and currentBit != 0UL) return true
            if (isOccupied(newPosition)) break
        }
    }
    return false
}

/**
 * Returns true if the given [Position] is under attack by a bishop on the given [board] using bitboard operations.
 */
fun BitBoard.isAttackedByBishop(position: Position, board: Board): Boolean {
    val directions = listOf(7, 9, -7, -9)
    val opponentBishops =
        if (board.isWhiteTurn()) blackPieces and pieces[Bishop::class]!! else whitePieces and pieces[Bishop::class]!!
    return isAttackedInDirections(position, directions, opponentBishops)
}

/**
 * Returns true if the given [Position] is under attack by a rook on the given [board] using bitboard operations.
 */
fun BitBoard.isAttackedByRook(position: Position, board: Board): Boolean {
    val directions = listOf(8, -8, 1, -1)
    val opponentRooks =
        if (board.isWhiteTurn()) blackPieces and pieces[Rook::class]!! else whitePieces and pieces[Rook::class]!!
    return isAttackedInDirections(position, directions, opponentRooks)
}

/**
 * Returns true if the given [Position] is under attack by a queen on the given [board] using bitboard operations.
 */
fun BitBoard.isAttackedByQueen(position: Position, board: Board): Boolean {
    val directions = listOf(8, -8, 1, -1, 7, 9, -7, -9)
    val opponentQueens =
        if (board.isWhiteTurn()) blackPieces and pieces[Queen::class]!! else whitePieces and pieces[Queen::class]!!
    return isAttackedInDirections(position, directions, opponentQueens)
}