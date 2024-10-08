package com.example.chess.ui.view

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.chess.adapter.ChessBoardAdapter
import com.example.chess.board.Square
import com.example.chess.model.imageResource
import com.example.chess.AppData.board
import com.example.chess.ui.manager.ChessSelectionManager
import com.example.chess.ui.manager.ModelViewRegistry
import kotlin.math.pow
import kotlin.math.sqrt


class ChessPieceView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private lateinit var square: Square
    private lateinit var chessBoardAdapter: ChessBoardAdapter

    private var touchStartX = 0f
    private var touchStartY = 0f
    private var dragThreshold = 10 // Move the piece only if the user moves their finger more than 10 pixels

    override fun performClick(): Boolean {
        super.performClick()
        if (square.piece?.player != board.currentPlayer) {
            return false
        }
        ChessSelectionManager.selectSquare(parent as ChessSquareView)

        return true
    }

    init {
        setupOnTouchListener()
    }

    private fun setupOnTouchListener() {
        setOnTouchListener { touchedView, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.x
                    touchStartY = event.y

                    if ((parent as ChessSquareView).state.value.isLegalMove) {
                        (parent as ChessSquareView).performClick()
                    }
                    touchedView.performClick()
                }

                MotionEvent.ACTION_MOVE -> {
                    val delta = sqrt(
                        (event.x - touchStartX.toDouble()).pow(2.0) + (event.y - touchStartY.toDouble()).pow(
                            2.0
                        )
                    ).toFloat()
                    if (delta < dragThreshold) {
                        return@setOnTouchListener false
                    }
                    val data = ClipData.newPlainText(
                        "piece",
                        touchedView.javaClass.simpleName
                    )
                    val chessPieceView = touchedView as ChessPieceView
                    if (!ModelViewRegistry.pieceViewMapper.contains(chessPieceView)) {
                        return@setOnTouchListener false
                    }
                    val shadowBuilder = DragShadowBuilder(touchedView)
                    touchedView.startDragAndDrop(data, shadowBuilder, touchedView, 0)
                    touchedView.visibility = INVISIBLE
                    true
                }

                MotionEvent.ACTION_UP -> {
                    touchedView.visibility = VISIBLE
                    true
                }

                else -> false
            }

        }
    }


    fun setSquare(square: Square) {
        this.square = square
        setImageResource(square.piece?.imageResource ?: 0)
    }

    fun setChessBoardAdapter(chessBoardAdapter: ChessBoardAdapter) {
        this.chessBoardAdapter = chessBoardAdapter
    }
}
