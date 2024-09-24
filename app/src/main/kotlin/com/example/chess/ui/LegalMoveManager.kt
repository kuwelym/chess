package com.example.chess.ui

import android.util.Log
import com.example.chess.ui.AppData.board

object LegalMoveManager {
    private var legalSquares: MutableSet<ChessSquareView> = mutableSetOf()

    /**
     * Sets the legal moves for the current piece.
     */
    fun setLegalMoves() {
        val piece = ChessSelectionManager.getSelectedSquare()?.getSquare()?.piece
        val startTime = System.currentTimeMillis()
        val legalMoves = piece?.generateMoves(board)
        val endTime = System.currentTimeMillis()
        Log.d("LegalMoveManager", "Time to generate moves: ${endTime - startTime}ms")
        legalMoves?.forEach { move ->
            val targetSquare = board.getSquare(move.dest)
            val targetSquareView = ModelViewRegistry.squareViewMapper.getViewForModel(targetSquare)
            legalSquares.add(targetSquareView)
            targetSquareView.setLegalMove(true)

            ModelViewRegistry.moveSquareViewMapper.register(move, targetSquareView)
        }
    }

    /**
     * Checks if the move is legal.
     */
    fun isLegalMove(squareView: ChessSquareView): Boolean {
        return squareView in legalSquares
    }

    /**
     * Clears the current legal moves.
     */
    fun clearLegalMoves() {
        legalSquares.forEach { it.setLegalMove(false) }
        legalSquares.clear()
    }
}