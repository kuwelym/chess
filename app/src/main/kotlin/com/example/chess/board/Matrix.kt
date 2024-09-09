package com.example.chess.board

/**
 * Represents a matrix of elements of type [T]
 */
class Matrix<T>(
    private val rows: Int,
    private val cols: Int,
    private val init: Matrix<T>.(Int, Int) -> T
) : Iterable<T>{
    val entries: List<List<T>> = List(rows) { row -> List(cols) { col -> init(row, col) } }

    override operator fun iterator(): Iterator<T> = object : Iterator<T>{
        private var row = 0
        private var col = 0

        override fun hasNext(): Boolean = row < rows && col < cols

        override fun next(): T {
            val value = entries[row][col]
            col++
            if (col == cols) {
                col = 0
                row++
            }
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (other !is Matrix<*>) return false

            if (rows != other.rows) return false
            if (cols != other.cols) return false
            if (entries != other.entries) return false

            return true
        }
    }
}

/**
 * Returns the particular [Square] on given [position] of the matrix
 */
operator fun <T> Matrix<T>.get(position: Position): T = entries[position.row][position.col]

/**
 * Performs the given [action] on each row of the matrix
 */
fun <T> Matrix<T>.forEachRow(action: (item: List<T>) -> Unit) {
    for (row in entries) {
        action(row)
    }
}

/**
 * Performs the given [action] on each element, providing both row and column index with the element
 */
fun <T> Matrix<T>.forEachIndexed(action: (row: Int, col: Int, item: T) -> Unit) {
    for ((rowIdx, row) in entries.withIndex()) {
        for ((colIdx, cell) in row.withIndex()) {
            action(rowIdx, colIdx, cell)
        }
    }
}