package com.example.chess.model

import com.example.chess.board.Position
import com.example.chess.board.Square
import com.example.chess.AppData.board

/** Abstract class representing a move in a chess game.
 * Including basic move, castling, promotion, en passant, etc.
 */
sealed class Move {
    abstract val piece: Piece
    abstract val dest: Position
    abstract val captured: Boolean
}

/** A basic move from one square to another.
 * @param piece the piece making the move
 * @param dest the destination square
 * @param captured whether the move captures a piece
 */
data class BasicMove(override val piece: Piece, override val dest: Position, override val captured: Boolean = false) : Move(){
    override fun toString(): String {
        return "${if (piece is Pawn && captured) piece.position.file else ""}${piece.symbol}${if(captured) "x" else ""}${dest.file}${dest.rank}${checkSymbol(board)}"
    }

    val isPromotion: Boolean
        get() = piece is Pawn && (dest.row == 0 || dest.row == 7)
}

/** A promotion move.
 * @param piece the pawn making the move
 * @param dest the destination square
 * @param captured whether the move captures a piece
 * @param promotion the piece to promote to
 */
data class PromotionMove(override val piece: Piece, override val dest: Position, override val captured: Boolean = false, val promotion: Piece) : Move(){
    override fun toString(): String {
        return "${piece.position.file}${if(captured) "x" else ""}${dest.file}${dest.rank}=${promotion.symbol}${checkSymbol(board)}"
    }
}

/** A castling move.
 * @param piece the king making the move
 * @param dest the destination square of the king
 * @param rook the rook making the move
 * @param rookDest the destination square of the rook
 * @param queenSide whether the move is queenSide castling
 */
data class CastlingMove(override val piece: Piece, override val dest: Position, val rook: Rook, val rookDest: Position, val queenSide: Boolean, override val captured: Boolean = false) : Move(){
    override fun toString(): String {
        return "${if(queenSide) "O-O-O" else "O-O"}${checkSymbol(board)}"
    }
}

/** An en passant move.
 * @param piece the pawn making the move
 * @param dest the destination square
 * @param capture the square of the captured pawn
 */
data class EnPassantMove(override val piece: Piece, override val dest: Position, val capture: Position,
                         override val captured: Boolean = true
) : Move() {
    override fun toString(): String {
        return "${piece.position.file}x${dest.file}${dest.rank} e.p.${checkSymbol(board)}"
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
            Square(rook.position, null),
            Square(dest, piece moveTo dest),
            Square(rookDest, rook moveTo rookDest)
        )
        is EnPassantMove -> listOf(
            Square(piece.position, null),
            Square(dest, piece moveTo dest),
            Square(capture, null)
        )
    }
}
