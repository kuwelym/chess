package com.example.chess.ui

import com.example.chess.board.Board
import com.example.chess.viewmodel.NotationViewModel

object AppData {
    var board : Board = Board.initialBoard()

    lateinit var notationViewModel: NotationViewModel

}