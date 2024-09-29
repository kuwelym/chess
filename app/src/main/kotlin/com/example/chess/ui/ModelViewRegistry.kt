package com.example.chess.ui

import android.view.View
import androidx.core.view.children
import com.example.chess.Move
import com.example.chess.Piece
import com.example.chess.board.Board
import com.example.chess.board.Square

interface BoardObserver {
    fun onMovePlayed(updatedSquares: List<Square>, board: Board)
}

class ModelViewRegistry<T, V : View> : ModelViewMapper<T, V>, BoardObserver {
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

    override fun clear() {
        modelToViewMap.clear()
        viewToModelMap.clear()
    }

    override fun unregister(model: T) {
        val view = modelToViewMap[model]
        modelToViewMap.remove(model)
        viewToModelMap.remove(view)
    }

    override fun onMovePlayed(updatedSquares: List<Square>, board: Board) {
        updatedSquares.forEach { square ->
            updateSquareView(square, board)
        }

    }

    private fun updateSquareView(square: Square, board: Board) {
        // Get previous square model and its view, its pieceView
        val previousSquare = board.previousBoard!!.getSquare(square.position)
        val squareView = squareViewMapper.getViewForModel(previousSquare)
        var pieceView = previousSquare.piece?.let {
            pieceViewMapper.getViewForModel(
                it
            )
        }

        // Unregister the previous square
        squareViewMapper.unregister(previousSquare)

        // Set that view to this the new square object
        squareView.setSquare(square)

        // If there is a piece, set the piece to the pieceView (captured)
        if (pieceView != null) {
            pieceViewMapper.unregister(previousSquare.piece!!)
            pieceView.setSquare(square)
        }

        // else find the square's pieceView and set the square to it
        else {
            squareView.children.find { it is ChessPieceView }?.let {
                (it as ChessPieceView).setSquare(square)
                pieceView = it
            }
        }

        // If the updated square has a piece, register the pieceView
        square.piece?.let {
            if (pieceView != null) {
                pieceViewMapper.register(it, pieceView!!)
            }
        }
        squareViewMapper.register(square, squareView)
    }

    companion object {
        val pieceViewMapper: ModelViewRegistry<Piece, ChessPieceView> by lazy {
            ModelViewRegistry()
        }

        val squareViewMapper: ModelViewRegistry<Square, ChessSquareView> by lazy {
            ModelViewRegistry()
        }

        val moveSquareViewMapper: ModelViewRegistry<Move, ChessSquareView> by lazy {
            ModelViewRegistry()
        }
    }
}