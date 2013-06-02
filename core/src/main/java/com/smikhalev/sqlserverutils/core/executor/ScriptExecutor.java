package com.smikhalev.sqlserverutils.core.executor;

import com.smikhalev.sqlserverutils.core.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ScriptExecutor extends StatementExecutor {

    public ScriptExecutor(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public void execute(String script) {
        try (Connection connection = getConnectionProvider().getConnection()) {
            executeScript(script, connection);
        } catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    private void executeScript(String script, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(script);
    }
}
