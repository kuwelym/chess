package com.example.chess.game

import com.example.chess.AppData.board
import com.example.chess.ui.manager.ChessSelectionManager
import com.example.chess.ui.view.ChessSquareView
import com.example.chess.ui.manager.LastMoveManager
import com.example.chess.ui.manager.LegalMoveManager
import com.example.chess.ui.manager.ModelViewRegistry

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