package com.example.chess

import com.example.chess.board.Board
import com.example.chess.board.Position
import com.example.chess.board.Square
import kotlinx.coroutines.runBlocking

/**
 * Returns true if the given [Position] is under attack by the opponent's pieces on the given [board].
 */
suspend fun Position.isUnderAttack(board: Board) = board.getPieces(board.currentPlayer.opponent())
    .flatMap { it.generateMoves(board = board, validateForCheck = false).await() }
    .filterIsInstance<BasicMove>()
    .any { it.dest == this }

/**
 * Returns true if the square is in check w.r.t given [board] state
 */
suspend fun Square.isUnderAttack(board: Board) = position.isUnderAttack(board)

/**
 * Returns true if the piece is in check w.r.t given [board] state
 */
suspend fun Piece.isUnderAttack(board: Board) = position.isUnderAttack(board)

/**
 * Returns true if the king of the player on turn is in check
 */
fun Board.isCheck() = runBlocking { getKing().position.isUnderAttack(this@isCheck) }

/**
 * Returns true if the king of the player on turn is not in check
 */
fun Board.isNotCheck() = !isCheck()

/**
 * Returns true if the king of the player on turn has been checkmated.
 * The king is in check and there are no legal moves for the current player.
 */
suspend fun Board.isCheckmate() =
    isCheck() && getPieces(currentPlayer).all { it.generateMoves(this).await().isEmpty() }

/**
 * Returns true if a stalemate occurred.
 * The king is not in check, but there are no legal moves for the current player.
 */
suspend fun Board.isStalemate() =
    !isCheck() && getPieces(currentPlayer).all { it.generateMoves(this).await().isEmpty() }