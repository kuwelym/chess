package com.example.chess.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chess.Move
import com.example.chess.board.Board
import com.example.chess.board.MovePair
import com.example.chess.board.Square
import com.example.chess.ui.AppData
import com.example.chess.ui.BoardObserver

class NotationViewModel: ViewModel(), BoardObserver {
    private val _playedNotations = MutableLiveData<List<MovePair>>()
    val playedNotations: LiveData<List<MovePair>>
        get() = _playedNotations

    init {
        _playedNotations.value = convertToMovePairs(AppData.board.playedMoves)
    }

    override fun onMovePlayed(updatedSquares: List<Square>, board: Board) {
        _playedNotations.value = convertToMovePairs(board.playedMoves)
    }


    private fun convertToMovePairs(moves: List<Move>): List<MovePair> {
        val movePairs = mutableListOf<MovePair>()
        for (i in moves.indices step 2) {
            val whiteMove = moves.getOrNull(i)
            val blackMove = moves.getOrNull(i + 1)
            movePairs.add(MovePair(i / 2 + 1, whiteMove, blackMove))
        }
        return movePairs
    }

}