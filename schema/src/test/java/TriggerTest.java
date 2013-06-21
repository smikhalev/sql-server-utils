import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TriggerTest extends BaseDatabaseContextTest {
    @Test
    public void testTriggerTable() throws Exception {
        Database expectedDatabase = new DatabaseBuilder()
                .addTable(new TableBuilder("main_table")
                        .addNotNullColumn("id", DbType.INT)
                        .addEmptyTrigger("some_trigger")
                        .build())
                .build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }
}
