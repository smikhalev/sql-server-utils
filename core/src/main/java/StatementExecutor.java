import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class StatementExecutor {

    private String connectionString;

    static {
        initSqlServerDriver();
    }

    public StatementExecutor(String connectionString) {
        this.connectionString = connectionString;
    }


    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private static void initSqlServerDriver() {
        try {
            Class.forName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
        } catch (ClassNotFoundException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }
}
