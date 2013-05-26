import java.sql.*;

public abstract class QueryExecutor<TResult> {
    private String connectionString;

    public QueryExecutor(String connectionString) {
        this.connectionString = connectionString;
    }

    public TResult execute(String query, Object ... parameters) {
        initSqlServerDriver();

        try (Connection connection = DriverManager.getConnection(connectionString)) {
            return executeQuery(query, connection, parameters);
        } catch (SQLException e) {
            throw new QueryExecutorException(e.getMessage(), e);
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
            throw new QueryExecutorException(e.getMessage(), e);
        }
    }

    protected abstract TResult processResult(ResultSet results) throws SQLException;

    private void initSqlServerDriver() {
        try {
            Class.forName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
        } catch (ClassNotFoundException e) {
            throw new QueryExecutorException(e.getMessage(), e);
        }
    }
}
