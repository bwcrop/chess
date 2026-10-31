package dataaccess.sql;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLDAOTests {
    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("alice", "hashedPassword", "alice@mail.com");
        userDAO.createUser(user);
        assertEquals(user, userDAO.getUser("alice"));
    }

    @Test
    void createUserDuplicateUsername() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "hash1", "alice@mail.com"));
        assertThrows(DataAccessException.class,
                () -> userDAO.createUser(new UserData("alice", "hash2", "other@mail.com")));
    }

    @Test
    void createUserMissingField() {
        assertThrows(DataAccessException.class,
                () -> userDAO.createUser(new UserData("alice", null, "alice@mail.com")));
    }

    @Test
    void getUserNotFound() throws DataAccessException {
        assertNull(userDAO.getUser("nobody"));
    }

    @Test
    void clearUsers() throws DataAccessException {
        userDAO.createUser(new UserData("alice", "hash", "alice@mail.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("alice"));
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "alice");
        authDAO.createAuth(auth);
        assertEquals(auth, authDAO.getAuth("token123"));
    }

    @Test
    void createAuthDuplicateToken() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "alice"));
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData("token123", "bob")));
    }

    @Test
    void getAuthNotFound() throws DataAccessException {
        assertNull(authDAO.getAuth("missing"));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "alice"));
        authDAO.deleteAuth("token123");
        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void deleteAuthLeavesOtherTokens() throws DataAccessException {
        authDAO.createAuth(new AuthData("token1", "alice"));
        authDAO.createAuth(new AuthData("token2", "alice"));
        authDAO.deleteAuth("token1");
        assertNotNull(authDAO.getAuth("token2"));
    }

    @Test
    void clearAuths() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "alice"));
        authDAO.clear();
        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void createGameStoresInitialBoard() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");
        GameData game = gameDAO.getGame(gameID);
        assertEquals("game1", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals(new ChessGame().getBoard(), game.game().getBoard());
        assertEquals(ChessGame.TeamColor.WHITE, game.game().getTeamTurn());
    }

    @Test
    void createGameUniqueIDs() throws DataAccessException {
        assertNotEquals(gameDAO.createGame("game1"), gameDAO.createGame("game2"));
    }

    @Test
    void createGameMissingName() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGameNotFound() throws DataAccessException {
        assertNull(gameDAO.getGame(999));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void updateGameAddsPlayers() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");
        GameData game = gameDAO.getGame(gameID);
        gameDAO.updateGame(new GameData(gameID, "alice", "bob", game.gameName(), game.game()));
        GameData updated = gameDAO.getGame(gameID);
        assertEquals("alice", updated.whiteUsername());
        assertEquals("bob", updated.blackUsername());
    }

    @Test
    void updateGameSavesMove() throws Exception {
        int gameID = gameDAO.createGame("game1");
        GameData game = gameDAO.getGame(gameID);
        ChessGame chessGame = game.game();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        gameDAO.updateGame(new GameData(gameID, null, null, game.gameName(), chessGame));

        ChessGame stored = gameDAO.getGame(gameID).game();
        assertEquals(chessGame.getBoard(), stored.getBoard());
        assertEquals(ChessGame.TeamColor.BLACK, stored.getTeamTurn());
        assertNull(stored.getBoard().getPiece(new ChessPosition(2, 5)));
        assertEquals(ChessPiece.PieceType.PAWN, stored.getBoard().getPiece(new ChessPosition(4, 5)).getPieceType());
    }

    @Test
    void updateGameNotFound() {
        GameData missing = new GameData(999, null, null, "missing", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(missing));
    }

    @Test
    void clearGames() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }
}

