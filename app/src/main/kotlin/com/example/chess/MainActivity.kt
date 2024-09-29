package com.example.chess

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chess.adapter.ChessNotationAdapter
import com.example.chess.databinding.ActivityMainBinding
import com.example.chess.factory.NotationViewModelFactory
import com.example.chess.viewmodel.NotationViewModel

class MainActivity : AppCompatActivity() {

    private val notationViewModel: NotationViewModel by viewModels{
        NotationViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = notationViewModel

        AppData.notationViewModel = notationViewModel

        val recyclerView = findViewById<RecyclerView>(R.id.moves_history_recycler_view)
        val adapter = ChessNotationAdapter()
        recyclerView.adapter = adapter

        notationViewModel.playedNotations.observe(this) { moves ->
            adapter.submitList(moves)
        }
    }
}