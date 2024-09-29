package com.example.chess.ui.manager

import com.example.chess.game.isCheckBitboard
import com.example.chess.game.isCheckmateBitboard
import com.example.chess.AppData.board
import com.example.chess.ui.view.ChessSquareView

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