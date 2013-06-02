package com.smikhalev.sqlserverutils.core.executor;

import com.smikhalev.sqlserverutils.core.ConnectionProvider;

import java.sql.*;

public abstract class ResultSetExecutor<TResult> extends StatementExecutor {
    public ResultSetExecutor(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public TResult execute(String query, Object ... parameters) {
        try (Connection connection = getConnectionProvider().getConnection()) {
            return executeQuery(query, connection, parameters);
        } catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    private TResult executeQuery(String query,  Connection connection, Object ... parameters) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }

        try (ResultSet results = statement.executeQuery()) {
            return processResult(results);
        } catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    protected abstract TResult processResult(ResultSet results) throws SQLException;

}
