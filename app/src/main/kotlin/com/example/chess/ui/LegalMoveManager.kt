package com.example.chess.ui

import android.util.Log
import com.example.chess.ui.AppData.board

object LegalMoveManager {
    private var legalSquares: Set<ChessSquareView> = emptySet()

    /**
     * Sets the legal moves for the current piece.
     */
    fun setLegalMoves() {
        val piece = ChessSelectionManager.getSelectedSquare()?.getSquare()?.piece
        val legalMoves = piece?.lastGeneratedMoves
        Log.d("LegalMoveManager", "Legal moves: $legalMoves")
        legalMoves?.forEach { move ->
            val targetSquare = board.getSquare(move.dest)
            Log.d("LegalMoveManager", "Target square: $targetSquare")
            val targetSquareView = DefaultModelViewMapper.squareViewMapper.getViewForModel(targetSquare)
            Log.d("LegalMoveManager", "Target square view: $targetSquareView")
            legalSquares += targetSquareView
            targetSquareView.setLegalMove(true)

            DefaultModelViewMapper.moveViewMapper.register(move, targetSquareView)
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
        legalSquares = emptySet()
    }
}