package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

abstract class SQLDAO {

    @FunctionalInterface
    protected interface RowReader<T> {
        T read(ResultSet rs) throws SQLException;
    }

    protected SQLDAO(String createTableStatement) throws DataAccessException {
        DatabaseManager.createDatabase();
        executeUpdate(createTableStatement);
    }

    protected final int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            setParameters(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + e.getMessage(), e);
        }
    }

    protected final int executeInsert(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + e.getMessage(), e);
        }
    }

    protected final <T> List<T> executeQuery(String statement, RowReader<T> reader, Object... params)
            throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(reader.read(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read from database: " + e.getMessage(), e);
        }
    }

    protected final <T> T executeQuerySingle(String statement, RowReader<T> reader, Object... params)
            throws DataAccessException {
        List<T> results = executeQuery(statement, reader, params);
        return results.isEmpty() ? null : results.get(0);
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}

