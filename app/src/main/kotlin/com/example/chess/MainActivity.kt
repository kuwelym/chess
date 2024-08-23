package com.example.chess

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.chess.board.Board
import com.example.chess.ui.ChessGridView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chessGridView = findViewById<ChessGridView>(R.id.chessboard)
        Log.d("MainActivity", "ChessGridView: ${chessGridView.childCount}")

    }
}