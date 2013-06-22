import com.smikhalev.sqlserverutils.core.ConnectionProvider;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"} )
public class DatabaseLoaderTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ConnectionProvider connectionProvider;

    @Autowired
    private DatabaseLoader databaseLoader;

    @Test
    public void simpleSchemaLoad() {
        Database database = databaseLoader.load();
        Assert.assertTrue(database != null);

        for (Table table : database.getTables().values())
        {
            String createScript = table.generateCreateScript();

            Assert.assertTrue(!createScript.isEmpty());
            System.out.println(createScript);
        }
    }
}
