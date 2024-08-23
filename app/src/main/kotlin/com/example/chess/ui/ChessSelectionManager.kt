package com.example.chess.ui

object ChessSelectionManager {
    private var selectedSquare: ChessSquareView? = null
    private var legalSquares: Set<ChessSquareView> = emptySet()

    /**
     * Selects a square, highlights it, and ensures only one square is selected.
     * @param square The square to select.
     */
    fun selectSquare(square: ChessSquareView) {
        // Deselect the previously selected square, if any
        clearSelection()

        // Select the new square
        selectedSquare = square
        square.setHighlighted(true)
        showLegalMoves()
    }

    /**
     * Clears the current selection.
     */
    private fun clearSelection() {
        selectedSquare?.setHighlighted(false)
        // Clear legal moves
        legalSquares.forEach { it.setLegalMove(false) }
        selectedSquare = null
    }

    /**
     * Returns the currently selected square, if any.
     */
    fun getSelectedSquare(): ChessSquareView? {
        return selectedSquare
    }

    private fun showLegalMoves() {
        // get ChessPieceView child
        val piece = selectedSquare?.getSquare()?.piece
        val legalMoves = piece?.generateMoves(AppData.board, true)
        legalMoves?.forEach { move ->
            val targetSquare = AppData.board.getSquare(move.dest)
            val targetSquareView = DefaultModelViewMapper.squareViewMapper.getViewForModel(targetSquare)
            legalSquares += targetSquareView
            targetSquareView.setLegalMove(true)
        }
    }

}
