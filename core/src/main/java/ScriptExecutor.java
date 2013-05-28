import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ScriptExecutor extends StatementExecutor {
    public ScriptExecutor(String connectionString) {
        super(connectionString);
    }

    public void execute(String script) {
        try (Connection connection = getConnection()) {
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
