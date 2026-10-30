package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO extends SQLDAO implements UserDAO {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

    public SQLUserDAO() throws DataAccessException {
        super(CREATE_TABLE);
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE users");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        executeUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?)",
                user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return executeQuerySingle("SELECT username, password, email FROM users WHERE username = ?",
                this::readUser, username);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
    }
}

