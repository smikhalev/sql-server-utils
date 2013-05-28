import java.sql.*;

public abstract class ResultSetExecutor<TResult> extends StatementExecutor {
    public ResultSetExecutor(String connectionString) {
        super(connectionString);
    }

    public TResult execute(String query, Object ... parameters) {
        try (Connection connection = getConnection()) {
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
