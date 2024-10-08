package com.example.chess.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chess.R
import com.example.chess.board.Position
import com.example.chess.board.Square
import com.example.chess.AppData.board
import com.example.chess.ui.view.ChessPieceView
import com.example.chess.ui.view.ChessSquareView
import com.example.chess.ui.manager.ModelViewRegistry


class ChessBoardAdapter(
    private val context: Context,
    private val squareSize: Int,
) : RecyclerView.Adapter<ChessBoardAdapter.ViewHolder>() {


    // Inflate the view for each cell
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(context, R.layout.chess_cell, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 64
    }

    private fun getItem(position: Int): Square {
        // position is from 0 to 63
        return board.getSquare(Position(position / 8, position % 8))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val square = getItem(position)
        holder.imageView.setSquare(square)
        holder.imageView.setChessBoardAdapter(this)
        holder.imageView.layoutParams = ConstraintLayout.LayoutParams(squareSize, squareSize)
        square.piece?.let { ModelViewRegistry.pieceViewMapper.register(it, holder.imageView) }

        ModelViewRegistry.squareViewMapper.register(square, holder.chessCellLayout)

        holder.chessCellLayout.setPosition(position)
        holder.chessCellLayout.setSquare(square)
        holder.chessCellLayout.layoutParams = ConstraintLayout.LayoutParams(squareSize, squareSize)

        // color the background of the cell based on the position
        val row = position / 8
        val col = position % 8

        val textSize = squareSize / 7f
        holder.rankTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        holder.fileTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        // number the ranks at the top left corner of the cell and the files at the bottom right corner, only for the first row and column
        holder.rankTextView.text = if (col == 0) (8 - row).toString() else ""
        holder.rankTextView.visibility = if (col == 0) View.VISIBLE else View.GONE

        holder.fileTextView.text = if (row == 7) ('a' + col).toString() else ""
        holder.fileTextView.visibility = if (row == 7) View.VISIBLE else View.GONE


    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var imageView: ChessPieceView
        var rankTextView: TextView
        var fileTextView: TextView
        var chessCellLayout: ChessSquareView

        init {
            view.findViewById<ChessPieceView>(R.id.chess_cell_image)!!.also { imageView = it }
            view.findViewById<TextView>(R.id.chess_cell_rank_text)!!.also { rankTextView = it }
            view.findViewById<TextView>(R.id.chess_cell_file_text)!!.also { fileTextView = it }
            view.findViewById<ChessSquareView>(R.id.chess_cell_layout)!!.also { chessCellLayout = it }
        }
    }

}

