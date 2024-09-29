package com.example.chess.ui

import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.chess.GameController
import com.example.chess.R
import com.example.chess.board.Square
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SquareState(
    val isHighlighted: Boolean = false,
    val isLastMove: Boolean = false,
    val isLegalMove: Boolean = false,
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false
)

class ChessSquareView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var position: Int = 0
    private lateinit var square: Square
    private var paint = Paint()
    private val squareColors = IntArray(64) { position ->
        getSquareColor(position) // Assuming you have 64 squares
    }


    private val _state = MutableStateFlow(SquareState())
    val state: StateFlow<SquareState> = _state.asStateFlow()

    init {
        setupDragAndDropListener()
    }

    private fun setupDragAndDropListener() {
        setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }

                DragEvent.ACTION_DROP -> {
                    if (LegalMoveManager.isLegalMove(this)) {
                        GameController.playMove(this)
                    } else {
                        return@setOnDragListener false
                    }

                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    (event.localState as View).visibility = VISIBLE
                    true
                }

                else -> false
            }
        }
    }

    fun setPosition(position: Int) {
        if (this.position != position) {
            this.position = position
            postInvalidateOnAnimation()
        }
    }

    fun setSquare(square: Square) {
        this.square = square
    }

    fun getSquare(): Square {
        return square
    }

    fun setHighlighted(highlighted: Boolean) {
        _state.value = _state.value.copy(isHighlighted = highlighted)
        postInvalidateOnAnimation()
    }


    fun setLastMove(lastMove: Boolean) {
        _state.value = _state.value.copy(isLastMove = lastMove)
        postInvalidateOnAnimation()
    }

    fun setLegalMove(legalMove: Boolean) {
        if (state.value.isLegalMove != legalMove) {
            _state.value = _state.value.copy(isLegalMove = legalMove)
            if (legalMove) {
                setOnClickListener {
                    GameController.playMove(this)
                }
            } else {
                setOnClickListener(null)
            }
            this.invalidate()
        }
    }

    fun setCheck(check: Boolean) {
        _state.value = _state.value.copy(isCheck = check)
        postInvalidateOnAnimation()
    }

    fun setCheckmate(checkmate: Boolean) {
        _state.value = _state.value.copy(isCheckmate = checkmate)
        postInvalidateOnAnimation()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(size, size)
    }

    private fun getSquareColor(position: Int): Int {
        val isLightSquare = (position / 8 + position % 8) % 2 == 0
        return if (isLightSquare) {
            ResourcesCompat.getColor(resources, R.color.light_square, null)
        } else {
            ResourcesCompat.getColor(resources, R.color.dark_square, null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val squareSize = measuredWidth.toFloat()
        setBackgroundColor(squareColors[position])

        if (state.value.isHighlighted) {
            paint.color = ResourcesCompat.getColor(resources, R.color.piece_selected_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (state.value.isLastMove) {
            paint.color = ResourcesCompat.getColor(resources, R.color.last_move_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (state.value.isLegalMove) {
            drawLegalMove(canvas)
        }
        if (state.value.isCheck) {
            paint.color = ResourcesCompat.getColor(resources, R.color.check_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
        if (state.value.isCheckmate) {
            paint.color = ResourcesCompat.getColor(resources, R.color.checkmate_color, null)
            canvas.drawRect(0f, 0f, squareSize, squareSize, paint)
        }
    }

    private fun drawLegalMove(canvas: Canvas) {
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.legal_moves_color, null)

        // Draw a ring if the move captures a piece
        if (ModelViewRegistry.moveSquareViewMapper.getModelForView(this).captured) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 12f // ring thickness
            canvas.drawCircle(
                width / 2f, height / 2f, minOf(width / 1.3f, height / 1.3f) / 2f, paint
            )
        } else {
            // Draw a solid circle if the square is empty
            paint.style = Paint.Style.FILL
            canvas.drawCircle(
                width / 2f, height / 2f, Integer.min(width / 2, height / 2) / 4f, paint
            )
        }
    }
}
