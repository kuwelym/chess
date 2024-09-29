package com.example.chess

import com.example.chess.ui.AppData.board
import com.example.chess.ui.ChessSelectionManager
import com.example.chess.ui.ChessSquareView
import com.example.chess.ui.LastMoveManager
import com.example.chess.ui.LegalMoveManager
import com.example.chess.ui.ModelViewRegistry

object GameController {
    fun playMove(squareView: ChessSquareView) {
        // Play the move
        board = board.playMove(ModelViewRegistry.moveSquareViewMapper.getModelForView(squareView))
        LastMoveManager.setLastMove(ChessSelectionManager.getSelectedSquare()!!, squareView)
        // Clear the legal moves
        LegalMoveManager.clearLegalMoves()
        ChessSelectionManager.clearSelection()
        ModelViewRegistry.moveSquareViewMapper.clear()
    }
}