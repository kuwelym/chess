package com.example.chess.ui

import com.example.chess.isCheckBitboard
import com.example.chess.isCheckmateBitboard
import com.example.chess.ui.AppData.board

object KingCheckManager {
    private var checkedKingSquare: ChessSquareView? = null

    fun setKingCheck() {
        clearKingCheck()

        if (board.isCheckBitboard()){
            checkedKingSquare = ModelViewRegistry.squareViewMapper.getViewForModel(board.getSquare(board.getKing().position))
            if (board.isCheckmateBitboard())
                checkedKingSquare?.setCheckmate(true)
            checkedKingSquare?.setCheck(true)
        }
    }

    private fun clearKingCheck() {
        checkedKingSquare?.setCheck(false)
        checkedKingSquare = null
    }
}