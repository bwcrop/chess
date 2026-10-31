package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDAO extends SQLDAO implements GameDAO {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (gameID)
            )""";

    private static final String SELECT_GAME = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

    private final Gson gson = new Gson();

    public SQLGameDAO() throws DataAccessException {
        super(CREATE_TABLE);
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE games");
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return executeInsert("INSERT INTO games (gameName, game) VALUES (?, ?)",
                gameName, gson.toJson(new ChessGame()));
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return executeQuerySingle(SELECT_GAME + " WHERE gameID = ?", this::readGame, gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return executeQuery(SELECT_GAME, this::readGame);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        int rows = executeUpdate(
                "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?",
                game.whiteUsername(), game.blackUsername(), game.gameName(), gson.toJson(game.game()), game.gameID());
        if (rows == 0) {
            throw new DataAccessException("game not found");
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        ChessGame chessGame = gson.fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"),
                rs.getString("gameName"), chessGame);
    }
}

