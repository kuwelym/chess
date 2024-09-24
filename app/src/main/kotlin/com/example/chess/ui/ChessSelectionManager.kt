package com.example.chess.ui

object ChessSelectionManager {
    private var selectedSquare: ChessSquareView? = null

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
        LegalMoveManager.setLegalMoves()
    }

    /**
     * Clears the current selection.
     */
    fun clearSelection() {
        selectedSquare?.setHighlighted(false)
        selectedSquare = null
        ModelViewRegistry.moveSquareViewMapper.clear()
        LegalMoveManager.clearLegalMoves()
    }

    /**
     * Returns the currently selected square, if any.
     */
    fun getSelectedSquare(): ChessSquareView? {
        return selectedSquare
    }

}
