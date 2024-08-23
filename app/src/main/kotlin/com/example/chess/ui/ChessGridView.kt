package com.example.chess.ui

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chess.ChessBoardAdapter
import com.example.chess.MainActivity
import com.example.chess.board.Board

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
        Log.d("ChessGridView", "Getting square size by device width")
        val activity = context as MainActivity
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = if (displayMetrics.widthPixels < displayMetrics.heightPixels)
            displayMetrics.widthPixels else displayMetrics.heightPixels
        return deviceWidth / 8
    }

    init {
        val gridLayoutManager = object : GridLayoutManager(context, 8) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layoutManager = gridLayoutManager
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val layoutParams = layoutParams
        layoutParams.width = squareSize * 8
        layoutParams.height = squareSize * 8
        setLayoutParams(layoutParams)
    }

}