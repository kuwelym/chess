package com.example.chess.ui

object LegalMoveManager {
    private var legalMoves: Set<ChessSquareView> = emptySet()

    /**
     * Sets the legal moves for the current piece.
     * @param moves The legal moves for the current piece.
     */
    fun setLegalMoves(moves: Set<ChessSquareView>) {
        legalMoves = moves

        ChessSelectionManager.getSelectedSquare()?.let { selectedSquare ->
            legalMoves.forEach { legalMove ->
                if (selectedSquare != legalMove) {
                    legalMove.setLegalMove(true)
                }
            }
        }
    }

    /**
     * Clears the current legal moves.
     */
    fun clearLegalMoves() {
        legalMoves.forEach { it.setLegalMove(false) }
        legalMoves = emptySet()
    }
}