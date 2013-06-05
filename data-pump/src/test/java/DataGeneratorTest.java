import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.DataTableExecutor;
import com.smikhalev.sqlserverutils.core.executor.ScriptExecutor;
import com.smikhalev.sqlserverutils.generator.ColumnGeneratorFactory;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.generator.SimpleDataGenerator;
import com.smikhalev.sqlserverutils.schema.*;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class DataGeneratorTest extends AbstractTestNGSpringContextTests {
    //@Autowired
    //private DataGenerator dataGenerator;

    @Autowired
    private ColumnGeneratorFactory columnGeneratorFactory;

    @Autowired
    private ScriptExecutor scriptExecutor;

    @Autowired
    private DataTableExecutor tableExecutor;

    @Test
    public void testAChunkLoading01() throws Exception {
        Database database = buildSimpleDabase();
        DataTable dataTable;
        final int rowCount = 1;
        final int chunkSize = 1;

        dataTable = loadDataTable(database, rowCount, chunkSize);

        Assert.assertNotNull(dataTable);
        Assert.assertEquals(rowCount, dataTable.getRows().get(0).get("rows_count"));
    }

    @Test
    public void testAChunkLoading02() throws Exception {
        Database database = buildSimpleDabase();
        DataTable dataTable;
        final int rowCount = 1;
        final int chunkSize = 2;

        dataTable = loadDataTable(database, rowCount, chunkSize);

        Assert.assertNotNull(dataTable);
        Assert.assertEquals(rowCount, dataTable.getRows().get(0).get("rows_count"));
    }

    @Test
    public void testAChunkLoading03() throws Exception {
        Database database = buildSimpleDabase();
        DataTable dataTable;
        final int rowCount = 2;
        final int chunkSize = 1;

        dataTable = loadDataTable(database, rowCount, chunkSize);

        Assert.assertNotNull(dataTable);
        Assert.assertEquals(rowCount, dataTable.getRows().get(0).get("rows_count"));
    }

    @Test
    public void testAChunkLoading04() throws Exception {
        Database database = buildSimpleDabase();
        DataTable dataTable;
        final int rowCount = 3;
        final int chunkSize = 2;

        dataTable = loadDataTable(database, rowCount, chunkSize);

        Assert.assertNotNull(dataTable);
        Assert.assertEquals(rowCount, dataTable.getRows().get(0).get("rows_count"));
    }

    @Test
    // In my laptop 100000 chunk for data generating looks like the best number
    // 7 sec for 1000000 of simple rows
    public void testAHugeGeneration() throws Exception {
        Database database = buildSimpleDabase();
        DataTable dataTable;
        final int rowCount = 1000000;
        final int chunkSize = 100000;

        dataTable = loadDataTable(database, rowCount, chunkSize);

        Assert.assertNotNull(dataTable);
        Assert.assertEquals(rowCount, dataTable.getRows().get(0).get("rows_count"));
    }

    @Test
    public void testAllColumnTypesGeneration() throws Exception {
        Database database = new DatabaseBuilder()
            .addTable(
                new TableBuilder("all_types_table")
                    .addNullColumn("bit_column", DbType.BIT)
                    .addNullColumn("tyinyint_column", DbType.TINYINT)
                    .addNullColumn("smallint_column", DbType.SMALLINT)
                    .addNullColumn("int_column", DbType.INT)
                    .addNullColumn("bigint_column", DbType.BIGINT)
                    .addNullColumn("float_column", DbType.FLOAT)
                    .addNullColumn("real_column", DbType.REAL)
                    .addNullColumn("nvarchar_column", DbType.NVARCHAR)
                    .addNullColumn("varchar_column", DbType.VARCHAR)
                    .addNullColumn("date_column", DbType.DATE)
                    .addNullColumn("time_column", DbType.TIME)
                    .addNullColumn("datetime_column", DbType.DATETIME)
                    .build()
            ).build();
        DataTable dataTable;
        final int rowCount = 10000;
        final int chunkSize = 100000;

        dataTable = loadDataTable(database, rowCount, chunkSize);

        Assert.assertNotNull(dataTable);
        Assert.assertEquals(rowCount, dataTable.getRows().get(0).get("rows_count"));
    }

    private DataTable loadDataTable(Database database, int rowCount, int chunkSize) throws Exception {
        DataTable dataTable;
        try (DatabaseContext dbContext = new DatabaseContext(database, scriptExecutor)) {
            dbContext.create();

            DataGenerator generator = new SimpleDataGenerator(columnGeneratorFactory, scriptExecutor, chunkSize);
            generator.generateData(database, rowCount);

            String query = String.format("select count(*) as rows_count from %s", database.getTables().get(0).getFullName());
            dataTable = tableExecutor.execute(query);
        }
        return dataTable;
    }

    private Database buildSimpleDabase() {
        Database database = new DatabaseBuilder()
                .addTable(
                        new TableBuilder("simple_table")
                                .addNullColumn("bigint_column", DbType.BIGINT)
                                .build()
                ).build();

        DataTable table;
        return database;
    }
}

