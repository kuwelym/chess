package com.example.chess.ui

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.chess.ChessBoardAdapter
import com.example.chess.Move
import com.example.chess.board.Square
import com.example.chess.imageResource
import com.example.chess.ui.AppData.board


class ChessPieceView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private lateinit var square: Square
    private lateinit var chessBoardAdapter: ChessBoardAdapter

    override fun performClick(): Boolean {
        super.performClick()
        if (square.piece == null) {
            return false
        }
        ChessSelectionManager.selectSquare(parent as ChessSquareView)
        val startTime = System.currentTimeMillis()
        val moveSet = square.piece?.generateMoves(board) ?: emptySet()
        Log.d("ChessPieceView", "Move set: $moveSet")

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        Log.d("ChessPieceView", "Move took $duration ms")
        movePiece(moveSet.first())

        return true
    }

    init {
        setOnTouchListener { touchedView, event ->
            Log.d("ChessBoardAdapter", "Touched view: $touchedView")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedView.performClick()
                    val data = ClipData.newPlainText(
                        "piece",
                        touchedView.javaClass.simpleName
                    )
                    val shadowBuilder = DragShadowBuilder(touchedView)
                    touchedView.startDragAndDrop(data, shadowBuilder, touchedView, 0)
                    touchedView.visibility = View.INVISIBLE
                    true
                }

                MotionEvent.ACTION_UP -> {
                    touchedView.visibility = View.VISIBLE
                    true
                }

                else -> false
            }

        }
    }


    fun setSquare(square: Square) {
        this.square = square
        setImageResource(square.piece.let { it?.imageResource } ?: 0)
    }

    fun setChessBoardAdapter(chessBoardAdapter: ChessBoardAdapter) {
        this.chessBoardAdapter = chessBoardAdapter
    }

    private fun movePiece(move: Move) {
        Log.d("ChessPieceView", "Moving piece: $move")
        // Move the piece
        board = board.playMove(move)

        chessBoardAdapter.notifyDataSetChanged()
        // Update the UI

    }
}
