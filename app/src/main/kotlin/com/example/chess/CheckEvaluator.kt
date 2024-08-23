package com.example.chess

import com.example.chess.board.Board
import com.example.chess.board.Position
import com.example.chess.board.Square

/**
 * Returns true if the position is in check w.r.t given [board] state
 */
fun Position.isInCheck(board: Board) = board.getPieces(board.currentPlayer.opponent())
    .flatMap { it.generateMoves(board = board, validateForCheck = false) }
    .filterIsInstance<BasicMove>()
    .any { it.dest == this }
/**
 * Returns true if the square is in check w.r.t given [board] state
 */
fun Square.isInCheck(board: Board) = position.isInCheck(board)

/**
 * Returns true if the piece is in check w.r.t given [board] state
 */
fun Piece.isInCheck(board: Board) = position.isInCheck(board)

/**
 * Returns true if the king of the player on turn is in check
 */
fun Board.isCheck() = getKing().position.isInCheck(this)

/**
 * Returns true if the king of the player on turn is not in check
 */
fun Board.isNotCheck() = !isCheck()

/**
 * Returns true if the king of the player on turn has been checkmated
 */
fun Board.isCheckmate() = isCheck() && getPieces(currentPlayer).all { it.generateMoves(this).isEmpty() }

/**
 * Returns true if the stalemate occurred
 */
fun Board.isStalemate() = !isCheck() && getPieces(currentPlayer).all { it.generateMoves(this).isEmpty() }