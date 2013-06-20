import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ForeignKeyTest extends BaseDatabaseContextTest {
    @Test
    public void testForeignKeyConstraintsTable() throws Exception {
        Table mainTable = new TableBuilder("main_table")
            .addNotNullColumn("id", DbType.INT)
            .addUniqueNonClusteredIndex("unique", "id")
            .build();
        Table refTable = new TableBuilder("referenced_table")
            .addNotNullColumn("id", DbType.INT)
            .addForeignKey("fk_main_ref", "id", mainTable)
            .build();
        Database expectedDatabase = new DatabaseBuilder()
                .addTable(mainTable)
                .addTable(refTable)
                .build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }


}
