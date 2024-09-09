package com.example.chess.ui

import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.chess.R
import com.example.chess.board.Square
import com.example.chess.ui.AppData.board

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
//                    val item = event.clipData.getItemAt(0)
//                    val dragData = item.text.toString()

                    val draggedView = event.localState as View
                    if (LegalMoveManager.isLegalMove(this)) {
                        playMove()
                    } else {
                        return@setOnDragListener false
                    }
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
    }

    private fun drawLegalMove(canvas: Canvas) {
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.legal_moves_color, null)

        // Set the stroke style if there's a piece to draw a ring
        if (square.piece != null) {
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
        board = board.playMove(DefaultModelViewMapper.moveViewMapper.getModelForView(this))
        // Clear the legal moves
        LegalMoveManager.clearLegalMoves()
        ChessSelectionManager.clearSelection()
        DefaultModelViewMapper.moveViewMapper.clear()
    }
}