package com.example.chess.ui

import android.util.Log
import android.view.View
import androidx.core.view.children
import com.example.chess.Move
import com.example.chess.Piece
import com.example.chess.board.Board
import com.example.chess.board.Square

interface BoardObserver {
    fun onMovePlayed(updatedSquares: List<Square>, board: Board)
}

class DefaultModelViewMapper<T, V: View> : ModelViewMapper<T, V>, BoardObserver{
    private val modelToViewMap = mutableMapOf<T, V>()
    private val viewToModelMap = mutableMapOf<V, T>()

    override fun getViewForModel(model: T): V {
        return modelToViewMap[model] ?: throw IllegalArgumentException("Model not found")
    }

    override fun getModelForView(view: V): T {
        return viewToModelMap[view] ?: throw IllegalArgumentException("View not found")
    }

    override fun register(model: T, view: V) {
        modelToViewMap[model] = view
        viewToModelMap[view] = model
    }

    override fun contains(model: T): Boolean {
        return modelToViewMap.containsKey(model)
    }

    override fun contains(view: V): Boolean {
        return viewToModelMap.containsKey(view)
    }

    fun clear() {
        modelToViewMap.clear()
        viewToModelMap.clear()
    }

    override fun unregister(model: T) {
        val view = modelToViewMap[model]
        modelToViewMap.remove(model)
        viewToModelMap.remove(view)
    }

    companion object {
        val pieceViewMapper: DefaultModelViewMapper<Piece, ChessPieceView> by lazy {
            DefaultModelViewMapper()
        }

        val squareViewMapper: DefaultModelViewMapper<Square, ChessSquareView> by lazy {
            DefaultModelViewMapper()
        }

        val moveViewMapper: DefaultModelViewMapper<Move, ChessSquareView> by lazy {
            DefaultModelViewMapper()
        }
    }

    override fun onMovePlayed(updatedSquares: List<Square>, board: Board) {

        updatedSquares.forEach { square ->

            val previousSquare = board.previousBoard!!.getSquare(square.position)
            // Update squareViewMapper
            val squareView = squareViewMapper.getViewForModel(previousSquare)
            squareView.setSquare(square)
            var pieceView = previousSquare.piece?.let {
                pieceViewMapper.getViewForModel(
                    it
                )
            }
            if (pieceView != null) {
                pieceView.setSquare(square)
            }
            else{
                squareView.children.find { it is ChessPieceView }?.let {
                    (it as ChessPieceView).setSquare(square)
                    pieceView = it
                }
            }
            square.piece?.let {
                if (pieceView != null) {
                    pieceViewMapper.register(it, pieceView!!)
                }
            }
            squareViewMapper.register(square, squareView)

        }

    }

}