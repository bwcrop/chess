package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) obj;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private static final int[][] KING_DIRECTIONS = {
            {1,0}, {1,1}, {1,-1}, {-1,0}, {-1,1}, {-1,-1}, {0,1}, {0,-1}
    };
    private static final int[][] QUEEN_DIRECTIONS = KING_DIRECTIONS;
    private static final int[][] ROOK_DIRECTIONS = {{1,0}, {-1,0}, {0,1}, {0,-1}};
    private static final int[][] BISHOP_DIRECTIONS = {{1,1}, {1,-1},{-1,1},{-1,-1}};
    private static final int[][] KNIGHT_DIRECTIONS = {
            {2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        switch (pieceType) {
            case KING -> addStepMoves(board, myPosition, possibleMoves, KING_DIRECTIONS);
            case KNIGHT -> addStepMoves(board, myPosition, possibleMoves, KNIGHT_DIRECTIONS);
            case QUEEN -> addSlidingMoves(board, myPosition, possibleMoves, QUEEN_DIRECTIONS);
            case ROOK -> addSlidingMoves(board, myPosition, possibleMoves, ROOK_DIRECTIONS);
            case BISHOP -> addSlidingMoves(board, myPosition, possibleMoves, BISHOP_DIRECTIONS);
            case PAWN -> addPawnMove(board, myPosition, possibleMoves);
        }

        return possibleMoves;
    }
    private boolean isInBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private void addStepMoves(ChessBoard board, ChessPosition myPosition,
                              Collection<ChessMove> moves, int[][] directions) {
        for (int[] dir : directions) {
            int row = myPosition.getRow() + dir[0];
            int col = myPosition.getColumn() + dir[1];
            if (!isInBounds(row, col)) {
                continue;
            }
            ChessPosition target = new ChessPosition(row, col);
            ChessPiece occupant = board.getPiece(target);
            if (occupant == null || occupant.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, target, null));
            }
        }
    }

    private void addSlidingMoves(ChessBoard board, ChessPosition myPosition,
                                 Collection<ChessMove> moves, int[][] directions) {
        for (int[] dir : directions) {
            int row = myPosition.getRow() + dir[0];
            int col = myPosition.getColumn() + dir[1];
            while (isInBounds(row, col)) {
                ChessPosition target = new ChessPosition(row,col);
                ChessPiece occupant = board.getPiece(target);
                if (occupant == null) {
                    moves.add(new ChessMove(myPosition, target, null));
                } else{
                    if (occupant.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
    }
    private void addPawnMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int col = myPosition.getColumn();
        int oneRow = myPosition.getRow() + direction;

        if (isInBounds(oneRow, col) && board.getPiece(new ChessPosition(oneRow, col)) == null) {
            addPawnMove(moves, myPosition, new ChessPosition(oneRow, col), promotionRow);

            if (myPosition.getRow() == startRow) {
                ChessPosition twoStep = new ChessPosition(myPosition.getRow()+ 2 * direction, col);
                if (board.getPiece(twoStep) == null) {
                    moves.add(new ChessMove(myPosition, twoStep,null));
                }
            }
        }

        for (int dc : new int []{-1,1}) {
            int targetCol = col + dc;
            if (!isInBounds(oneRow, targetCol)) {
                continue;
            }
            ChessPosition target = new ChessPosition(oneRow, targetCol);
            ChessPiece occupant = board.getPiece(target);
            if (occupant != null && occupant.getTeamColor() != pieceColor) {
                addPawnMove(moves, myPosition, target, promotionRow);
            }
        }
    }
    private void addPawnMove(Collection<ChessMove> moves, ChessPosition from, ChessPosition to, int promotionRow) {
        if (to.getRow() == promotionRow) {
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }
}
