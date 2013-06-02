import com.smikhalev.sqlserverutils.core.executor.ScriptExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"} )
public class BaseDatabaseContextTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private DatabaseLoader databaseLoader;

    @Autowired
    private ScriptExecutor scriptExecutor;

    protected Database loadDatabase(Database sourceDatabase) throws Exception {
        try (DatabaseContext dbContext = new DatabaseContext(sourceDatabase, scriptExecutor)) {
            dbContext.create();
            return databaseLoader.load();
        }
    }
}