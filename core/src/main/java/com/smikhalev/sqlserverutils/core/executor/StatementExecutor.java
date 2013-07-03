package com.smikhalev.sqlserverutils.core.executor;

import com.smikhalev.sqlserverutils.core.ConnectionProvider;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.core.ResultSetProcessor;

import java.sql.*;

public class StatementExecutor {

    private final ConnectionProvider connectionProvider;

    static {
        initSqlServerDriver();
    }

    public StatementExecutor(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public void processResultSet(ResultSetProcessor processor, String query, Object... parameters) {
        try (Connection connection = getConnectionProvider().getConnection()) {
            PreparedStatement statement = prepareStatement(query, connection, parameters);
            processResultSet(processor, statement);
        } catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    public void executeScript(String script) {
        try (Connection connection = getConnectionProvider().getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(script);
        } catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    public void executeBatch(String batch) {
        try (Connection connection = getConnectionProvider().getConnection()) {
            Statement statement = connection.createStatement();
            String[] scripts = batch.split(Constants.GO);
            for(String script : scripts) {
                statement.execute(script);
        }
        } catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    public DataTable executeAsDataTable(String query, Object... parameters) {
        DataTableResultSetProcessor processor = new DataTableResultSetProcessor();
        processResultSet(processor, query, parameters);
        return processor.getResult();
    }

    private static void initSqlServerDriver() {
        try {
            Class.forName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
        } catch (ClassNotFoundException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    private PreparedStatement prepareStatement(String query, Connection connection, Object[] parameters) throws SQLException {
        PreparedStatement statement;
        statement = connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
        return statement;
    }

    private void processResultSet(ResultSetProcessor processor, PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            processor.process(resultSet);
        }
        catch (SQLException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }
}
