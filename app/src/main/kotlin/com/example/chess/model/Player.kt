package com.example.chess.model

enum class Player {
    WHITE,
    BLACK
}

fun Player.opponent(): Player {
    return when (this) {
        Player.WHITE -> Player.BLACK
        Player.BLACK -> Player.WHITE
    }
}