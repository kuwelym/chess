package com.example.chess.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chess.adapter.ChessBoardAdapter

class ChessGridView: RecyclerView {

    private val squareSize: Int

    constructor(context: Context) : super(context) {
        this.squareSize = getSquareSizeByDeviceWidth(context)
        adapter = ChessBoardAdapter(context, squareSize)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.squareSize = getSquareSizeByDeviceWidth(context)
        adapter = ChessBoardAdapter(context, squareSize)
    }

    private fun getSquareSizeByDeviceWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val deviceWidth = if (displayMetrics.widthPixels < displayMetrics.heightPixels)
            displayMetrics.widthPixels else displayMetrics.heightPixels
        return deviceWidth / 8
    }

    init {
        val gridLayoutManager = object : GridLayoutManager(context, 8, VERTICAL, true) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layoutManager = gridLayoutManager
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        layoutParams = layoutParams.apply {
            width = squareSize * 8
            height = squareSize * 8
        }
    }

}