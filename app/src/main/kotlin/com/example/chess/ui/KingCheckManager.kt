package com.example.chess.ui

import com.example.chess.isUnderAttack
import com.example.chess.ui.AppData.board

object KingCheckManager {
    private var kingCheckSquare: ChessSquareView? = null

    fun setKingCheck() {
        clearKingCheck()
        val king = board.getKing()
        if (king.isUnderAttack(board)){
            kingCheckSquare = ModelViewRegistry.squareViewMapper.getViewForModel(board.getSquare(board.getKing().position))
            kingCheckSquare?.setCheck(true)
        }
    }

    private fun clearKingCheck() {
        kingCheckSquare?.setCheck(false)
        kingCheckSquare = null
    }
}