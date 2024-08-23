package com.example.chess

import com.example.chess.board.Position
import com.example.chess.board.Square

/** Abstract class representing a move in a chess game.
 * Including basic move, castling, promotion, en passant, etc.
 */
sealed class Move {
    abstract val piece: Piece
    abstract val dest: Position
}

/** A basic move from one square to another.
 * @param piece the piece making the move
 * @param dest the destination square
 * @param capture whether the move captures a piece
 */
data class BasicMove(override val piece: Piece, override val dest: Position, val capture: Boolean = false) : Move(){
    override fun toString(): String {
        return "${if (piece is Pawn && capture) piece.position.file else ""}${piece.symbol}${if(capture) "x" else ""}${dest.file}${dest.rank}"
    }

    val isPromotion: Boolean
        get() = piece is Pawn && (dest.row == 0 || dest.row == 7)
}

/** A promotion move.
 * @param piece the pawn making the move
 * @param dest the destination square
 * @param capture whether the move captures a piece
 * @param promotion the piece to promote to
 */
data class PromotionMove(override val piece: Piece, override val dest: Position, val capture: Boolean = false, val promotion: Piece) : Move(){
    override fun toString(): String {
        return "${piece.position.file}${if(capture) "x" else ""}${dest.file}${dest.rank}=${promotion.symbol}"
    }
}

/** A castling move.
 * @param piece the rook making the move
 * @param dest the destination square of the rook
 * @param king the king making the move
 * @param kingDest the destination square of the king
 * @param queenSide whether the move is queenSide castling
 */
data class CastlingMove(override val piece: Piece, override val dest: Position, val king: Piece, val kingDest: Position, val queenSide: Boolean) : Move(){
    override fun toString(): String {
        return if(queenSide) "O-O-O" else "O-O"
    }
}

/** An en passant move.
 * @param piece the pawn making the move
 * @param dest the destination square
 * @param capture the square of the captured pawn
 */
data class EnPassantMove(override val piece: Piece, override val dest: Position, val capture: Position) : Move() {
    override fun toString(): String {
        return "${piece.position.file}x${dest.file}${dest.rank} e.p."
    }
}

/** Returns the squares impacted by the move.
 * @return a list of squares impacted by the move
 * including the starting square, destination square, and captured square
 */
fun Move.getImpactedSquares(): List<Square> {
    return when(this){
        is BasicMove -> listOf(
            Square(piece.position, null),
            Square(dest, piece moveTo dest)
        )
        is PromotionMove -> {
            val (pawn, des, _, promotion) = this
            listOf(
                Square(pawn.position, null),
                Square(des, promotion)
            )
        }
        is CastlingMove -> listOf(
            Square(piece.position, null),
            Square(king.position, null),
            Square(dest, piece moveTo dest),
            Square(kingDest, king moveTo kingDest)
        )
        is EnPassantMove -> listOf(
            Square(piece.position, null),
            Square(dest, piece moveTo dest),
            Square(capture, null)
        )

    }
}
