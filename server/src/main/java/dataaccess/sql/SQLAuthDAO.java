package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO extends SQLDAO implements AuthDAO {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS auths (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )""";

    public SQLAuthDAO() throws DataAccessException {
        super(CREATE_TABLE);
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE auths");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        executeUpdate("INSERT INTO auths (authToken, username) VALUES (?, ?)", auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return executeQuerySingle("SELECT authToken, username FROM auths WHERE authToken = ?",
                this::readAuth, authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        executeUpdate("DELETE FROM auths WHERE authToken = ?", authToken);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        return new AuthData(rs.getString("authToken"), rs.getString("username"));
    }
}

