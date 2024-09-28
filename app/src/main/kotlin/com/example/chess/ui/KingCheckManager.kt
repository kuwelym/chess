package com.example.chess.ui

import com.example.chess.isCheckBitboard
import com.example.chess.ui.AppData.board

object KingCheckManager {
    private var kingCheckSquare: ChessSquareView? = null

    fun setKingCheck() {
        clearKingCheck()

        if (board.isCheckBitboard()){
            kingCheckSquare = ModelViewRegistry.squareViewMapper.getViewForModel(board.getSquare(board.getKing().position))
            kingCheckSquare?.setCheck(true)
        }
    }

    private fun clearKingCheck() {
        kingCheckSquare?.setCheck(false)
        kingCheckSquare = null
    }
}