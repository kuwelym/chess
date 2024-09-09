package com.example.chess.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chess.viewmodel.NotationViewModel

class NotationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotationViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}