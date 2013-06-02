import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"} )
public class DatabaseLoaderTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DatabaseLoader databaseLoader;

    @Test
    public void simpleSchemaLoad() {
        Database database = databaseLoader.load();

        for (Table table : database.getTables())
        {
            String createScript = table.generateCreateScript();
            System.out.println(createScript);
        }
    }
}
