package com.example.chess.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chess.board.MovePair
import com.example.chess.databinding.NotationCellBinding

class ChessNotationAdapter : ListAdapter<MovePair, ChessNotationAdapter.NotationViewHolder>(
    MovePairDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotationViewHolder {
        val binding = NotationCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotationViewHolder(private val binding: NotationCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movePair: MovePair) {
            binding.notationText.text = "${movePair.moveNumber}. ${movePair.whiteMove?.toString() ?: ""} ${movePair.blackMove?.toString() ?: ""}"
        }
    }

    class MovePairDiffCallback : DiffUtil.ItemCallback<MovePair>() {
        override fun areItemsTheSame(oldItem: MovePair, newItem: MovePair): Boolean {
            return oldItem.moveNumber == newItem.moveNumber
        }

        override fun areContentsTheSame(oldItem: MovePair, newItem: MovePair): Boolean {
            return oldItem == newItem
        }
    }
}

@BindingAdapter("playedNotations")
fun bindPlayedMoves(recyclerView: RecyclerView, movePairs: List<MovePair>?) {
    val adapter = recyclerView.adapter as? ChessNotationAdapter
    adapter?.submitList(movePairs)
}