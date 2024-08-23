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
import androidx.core.view.children
import com.example.chess.R
import com.example.chess.board.Square

class ChessSquareView : ConstraintLayout {
    private var position: Int = 0
    private var isHighlighted: Boolean = false
    private var isLastMove: Boolean = false
    private var isLegalMove: Boolean = false
    private var isCheck: Boolean = false
    private lateinit var square: Square

    private var paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setOnDragListener { targetView, event ->
            when (event.action) {

                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    true
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val dragData = item.text.toString()
                    Log.d("ChessBoardAdapter", "Dropped $dragData")

                    val draggedView = event.localState as View
                    val draggedViewParent = draggedView.parent as ViewGroup
                    val dropTarget = targetView as ViewGroup
                    draggedViewParent.removeView(draggedView)
                    // remove previous view
                    for (i in 0 until dropTarget.childCount) {
                        val child = dropTarget.getChildAt(i)
                        if (child is ChessPieceView) {
                            dropTarget.removeView(child)
                        }
                    }

                    dropTarget.addView(draggedView)

                    draggedView.visibility = View.INVISIBLE
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    val draggedView = event.localState as View
                    draggedView.visibility = View.VISIBLE

                    true
                }

                else -> false
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
        if (isHighlighted != highlighted) {
            isHighlighted = highlighted
            this.invalidate()
        }
    }

    fun setLastMove(lastMove: Boolean) {
        if (isLastMove != lastMove) {
            isLastMove = lastMove
            this.invalidate()
        }
    }

    fun setLegalMove(legalMove: Boolean) {
        if (isLegalMove != legalMove) {
            isLegalMove = legalMove
            this.invalidate()
        }
    }

    fun setCheck(check: Boolean) {
        if (isCheck != check) {
            isCheck = check
            this.invalidate()
        }
    }

    fun getPosition(): Int {
        return position
    }

    fun isHighlighted(): Boolean {
        return isHighlighted
    }

    fun isLastMove(): Boolean {
        return isLastMove
    }

    fun isLegalMove(): Boolean {
        return isLegalMove
    }

    fun isCheck(): Boolean {
        return isCheck
    }

    fun clear() {
        isHighlighted = false
        isLastMove = false
        isLegalMove = false
        isCheck = false
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

        if (isHighlighted) {
            paint.color = ResourcesCompat.getColor(resources, R.color.piece_selected_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (isLastMove) {
            paint.color = ResourcesCompat.getColor(resources, R.color.last_move_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (isLegalMove) {
            drawLegalMove(canvas)
        }
        if (isCheck) {
            paint.color = Color.RED
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if(square.piece == null) {
            // find first child that is a ChessPieceView
            children.find { it is ChessPieceView }?.visibility = View.INVISIBLE
        }
    }

    private fun drawLegalMove(canvas: Canvas) {
        // Draw a round circle around middle
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.legal_moves_color, null)
        canvas.drawCircle(width / 2f, height / 2f, Integer.min(width / 2, height / 2) / 4f, paint)
    }

    private fun playMove() {
        // Move the piece
        AppData.board = AppData.board.playMove(DefaultModelViewMapper.moveViewMapper.getModelForView(this))
        // Clear the legal moves
        DefaultModelViewMapper.moveViewMapper.clear()
    }
}