import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"} )
public class BaseDatabaseContextTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private DatabaseLoader databaseLoader;

    @Autowired
    private StatementExecutor executor;

    protected Database loadDatabase(Database sourceDatabase) throws Exception {
        try (DatabaseContext dbContext = new DatabaseContext(sourceDatabase, executor)) {
            dbContext.create();
            return databaseLoader.load();
        }
    }
}