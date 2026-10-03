package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) obj;
        return gameOver == chessGame.gameOver && teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, gameOver);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                ", gameOver=" + gameOver +
                "}";
    }

    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();

    private boolean gameOver;

    public ChessGame() {
        this.board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    public boolean isOver() {
        return gameOver;
    }

    public void setOver(boolean over) {
        this.gameOver = over;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = board.getPiece(startPosition);
        if (myPiece == null) {
            return null;
        }

        Collection<ChessMove> potentialMoves = myPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> legal = new ArrayList<>();

        for (ChessMove possibleMove : potentialMoves) {
            ChessPosition end = possibleMove.getEndPosition();
            ChessPiece targetPiece = board.getPiece(end);
            board.addPiece(startPosition, null);
            board.addPiece(end, myPiece);
            try {
                if (!isInCheck(myPiece.getTeamColor())) {
                    legal.add(possibleMove);
                }
            } finally {
                board.addPiece(end, targetPiece);
                board.addPiece(startPosition, myPiece);
            }
        }
        return legal;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());

        if (pieceToMove == null || pieceToMove.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        if (move.getPromotionPiece() != null) {
            pieceToMove = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), pieceToMove);

        TeamColor next = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        setTeamTurn(next);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        search:
        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece potentialPiece = board.getPiece(position);
                if (potentialPiece != null && potentialPiece.getTeamColor() == teamColor
                        && potentialPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                    break search;
                }
            }
        }

        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece enemyPiece = board.getPiece(position);
                if (enemyPiece == null || enemyPiece.getTeamColor() == teamColor) {
                    continue;
                }

                for (ChessMove enemyMove : enemyPiece.pieceMoves(board, position)) {
                    if (enemyMove.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition targetPosition = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(targetPosition);

                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }

                Collection<ChessMove> moves = validMoves(targetPosition);
                if (moves != null && !moves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition targetPosition = new ChessPosition(row, column);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                if (targetPiece != null && teamColor == targetPiece.getTeamColor()) {
                    Collection<ChessMove> moves = validMoves(targetPosition);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
