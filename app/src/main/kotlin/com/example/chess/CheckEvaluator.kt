package com.example.chess

import com.example.chess.board.BitBoard
import com.example.chess.board.Board
import com.example.chess.board.Position
import com.example.chess.board.Square

/**
 * Returns true if the given [Position] is under attack by the opponent's pieces on the given [board].
 */
fun Position.isUnderAttack(board: Board) = board.getPieces(board.currentPlayer.opponent())
    .flatMap { it.generateMoves(board = board, validateForCheck = false) }
    .filterIsInstance<BasicMove>()
    .any { it.dest == this }

private fun BitBoard.isKingInCheck(player: Player): Boolean {
    val kings = pieces[King::class]!!
    val kingBitboard = if (player == Player.WHITE) kings and whitePieces else kings and blackPieces
    val opponentPieces = if (player == Player.WHITE) blackPieces else whitePieces

    // Check if any opponent piece can attack the king
    // This method should use bitboard operations to determine if the king is in check


    return false // Placeholder, implement actual check logic
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