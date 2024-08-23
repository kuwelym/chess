package com.example.chess.ui

import android.view.View
import com.example.chess.Move
import com.example.chess.Piece
import com.example.chess.board.Square

class DefaultModelViewMapper<T, V: View> : ModelViewMapper<T, V> {
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

}
