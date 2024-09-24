package com.example.chess.ui

import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEachIndexed
import com.example.chess.R
import com.example.chess.board.Square
import com.example.chess.ui.AppData.board

data class SquareState(
    val isHighlighted: Boolean = false,
    val isLastMove: Boolean = false,
    val isLegalMove: Boolean = false,
    val isCheck: Boolean = false
)

class ChessSquareView : ConstraintLayout {
    private var position: Int = 0
    private lateinit var square: Square
    private var paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var state = SquareState()

    init {
        setOnDragListener { targetView, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }

                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as View
                    if (LegalMoveManager.isLegalMove(this)) {
                        playMove()
                    } else {
                        return@setOnDragListener false
                    }
                    val draggedViewParent = draggedView.parent as ViewGroup
                    val dropTarget = targetView as ViewGroup
                    draggedViewParent.removeView(draggedView)
                    removeChessPieceViewFrom(dropTarget)
                    dropTarget.addView(draggedView)
                    draggedView.visibility = View.INVISIBLE
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    (event.localState as View).visibility = View.VISIBLE
                    true
                }

                else -> false
            }
        }
    }

    private fun removeChessPieceViewFrom(parent: ViewGroup) {
        parent.forEachIndexed { index, child ->
            if (child is ChessPieceView) {
                parent.removeViewAt(index)
                return
            }
        }
    }

    fun setPosition(position: Int) {
        if (this.position != position) {
            this.position = position
            this.invalidate()
        }
    }

    fun setSquare(square: Square) {
        this.square = square
    }

    fun getSquare(): Square {
        return square
    }

    fun setHighlighted(highlighted: Boolean) {
        state = state.copy(isHighlighted = highlighted)
        invalidate()
    }

    fun setLastMove(lastMove: Boolean) {
        state = state.copy(isLastMove = lastMove)
        invalidate()
    }

    fun setLegalMove(legalMove: Boolean) {
        if (state.isLegalMove != legalMove) {
            state = state.copy(isLegalMove = legalMove)
            if (legalMove) {
                setOnClickListener {
                    playMove()
                }
            } else {
                setOnClickListener(null)
            }
            this.invalidate()
        }
    }

    fun setCheck(check: Boolean) {
        state = state.copy(isCheck = check)
        invalidate()
    }

    fun getPosition(): Int {
        return position
    }

    fun clear() {
        state = SquareState()
        this.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val squareSize = measuredWidth.toFloat()
        setBackgroundColor(if ((position / 8 + position % 8) % 2 == 0) Color.WHITE else Color.GRAY)

        if (state.isHighlighted) {
            paint.color = ResourcesCompat.getColor(resources, R.color.piece_selected_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (state.isLastMove) {
            paint.color = ResourcesCompat.getColor(resources, R.color.last_move_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (state.isLegalMove) {
            drawLegalMove(canvas)
        }
        if (state.isCheck) {
            paint.color = ResourcesCompat.getColor(resources, R.color.check_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
    }

    private fun drawLegalMove(canvas: Canvas) {
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.legal_moves_color, null)

        // Draw a ring if the move captures a piece
        if (ModelViewRegistry.moveSquareViewMapper.getModelForView(this).captured) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 12f // Adjust the ring thickness here
            canvas.drawCircle(
                width / 2f,
                height / 2f,
                minOf(width / 1.3f, height / 1.3f) / 2f,
                paint
            )
        } else {
            // Draw a solid circle if the square is empty
            paint.style = Paint.Style.FILL
            canvas.drawCircle(
                width / 2f,
                height / 2f,
                Integer.min(width / 2, height / 2) / 4f,
                paint
            )
        }
    }

    private fun playMove() {
        // Play the move
        board = board.playMove(ModelViewRegistry.moveSquareViewMapper.getModelForView(this))
        LastMoveManager.setLastMove(ChessSelectionManager.getSelectedSquare()!!, this)
        // Clear the legal moves
        LegalMoveManager.clearLegalMoves()
        ChessSelectionManager.clearSelection()
        ModelViewRegistry.moveSquareViewMapper.clear()
        Log.d("ChessSquareView", "Played move: $board")
    }
}
