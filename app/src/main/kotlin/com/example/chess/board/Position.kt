package com.example.chess.board

data class Position(val row: Int, val col: Int){
    /*
    The rank of a square is the row number of the square, counting from 1 to 8 from the white side.
     */
    val rank: Int by lazy { 8 - row }

    /*
    The file of a square is the column number of the square, counting from a to h from the white side.
     */

    val file: Char by lazy { 'a' + col }

    val isValid: Boolean
        get() = row in 0..7 && col in 0..7
}

operator fun Position.plus(other: Position): Position {
    return Position(row + other.row, col + other.col)
}

operator fun Position.minus(other: Position): Position {
    return Position(row - other.row, col - other.col)
}

operator fun Position.plus(other: Pair<Int, Int>): Position {
    return Position(row + other.first, col + other.second)
}

operator fun Position.minus(other: Pair<Int, Int>): Position {
    return Position(row - other.first, col - other.second)
}

operator fun Position.times(value: Int): Position {
    return Position(row * value, col * value)
}

