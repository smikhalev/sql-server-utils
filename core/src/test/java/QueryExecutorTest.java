import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class QueryExecutorTest {
    private final String connectionString = "jdbc:jtds:sqlserver://mikhalevpc/mini-pump;user=sa;password=sa2013;";

    @Test
    public void executeSimpleQueryTest() {
        QueryExecutor<HashMap<String, String>> executor = new QueryExecutor<HashMap<String, String>>(connectionString) {
            @Override
            protected HashMap<String, String> processResult(ResultSet results) throws SQLException {
                HashMap<String, String> firstRow = new HashMap<>();

                firstRow.put(results.getString("id"), results.getString("name"));
                return firstRow;
            }
        };

        executor.execute("select id,name from simple_table");
    }
}
