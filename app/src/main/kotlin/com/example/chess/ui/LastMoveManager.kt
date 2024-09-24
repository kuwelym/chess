package com.example.chess.ui

object LastMoveManager {
    private var lastMoveSquareViews: Pair<ChessSquareView, ChessSquareView>? = null

    fun setLastMove(previousSquareView: ChessSquareView, newSquareView: ChessSquareView) {
        clearLastMove()
        previousSquareView.setLastMove(true)
        newSquareView.setLastMove(true)
        KingCheckManager.setKingCheck()
        lastMoveSquareViews = Pair(previousSquareView, newSquareView)
    }

    private fun clearLastMove() {
        lastMoveSquareViews?.first?.setLastMove(false)
        lastMoveSquareViews?.second?.setLastMove(false)
        lastMoveSquareViews = null
    }


}