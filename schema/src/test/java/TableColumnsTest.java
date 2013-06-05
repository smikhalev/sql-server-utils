import com.smikhalev.sqlserverutils.core.executor.StatementExecutorException;
import com.smikhalev.sqlserverutils.schema.*;
import com.smikhalev.sqlserverutils.schema.dbobjects.Column;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TableColumnsTest extends BaseDatabaseContextTest {

    @Test
    public void testSimplestTable() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .addNotNullColumn("bit_column_not_null", DbType.BIT)
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test (expectedExceptions = StatementExecutorException.class,
           expectedExceptionsMessageRegExp = "Column names in each table must be unique.*")
    public void testColumnsWithTheSameNameTable() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testOneTableWithAllColumnTypes() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
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
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testSizeOfColumnTypes() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("all_types_table")
                        .addNullColumn("nvarchar_column", DbType.NVARCHAR, 50)
                        .addNullColumn("varchar_column", DbType.VARCHAR, 10)
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testSomeTablesTable() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
               ).addTable(
                new TableBuilder("second_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test (expectedExceptions = StatementExecutorException.class,
            expectedExceptionsMessageRegExp = "There is already an object named.*")
    public void testSomeTablesWithTheSameNameTable() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
               ).addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testNegativeDifferentTableNames() throws Exception {
        Database sourceDatabase = buildTypicalSimpleTable();
        Database expectedDatabase = buildTypicalSimpleTable();

        Table expectedTable = expectedDatabase.getTables().get(0);
        expectedTable.setName("wrongName");

        Database actualDatabase = loadDatabase(sourceDatabase);
        Assert.assertNotEquals(actualDatabase.getTables().get(0), expectedTable, "This table should not be the same");
    }

    @Test
    public void testNegativeDifferentColumnNames() throws Exception {
        Database sourceDatabase = buildTypicalSimpleTable();
        Database expectedDatabase = buildTypicalSimpleTable();

        Column expectedColumn = expectedDatabase.getTables().get(0).getColumns().get(0);
        expectedColumn.setName("wrong name");

        Database actualDatabase = loadDatabase(sourceDatabase);
        Column actualColumn = actualDatabase.getTables().get(0).getColumns().get(0);
        Assert.assertNotEquals(actualColumn, expectedColumn, "This column should not be the same");
    }

    @Test
    public void testNegativeDifferentColumnTypes() throws Exception {
        Database sourceDatabase = buildTypicalSimpleTable();
        Database expectedDatabase = buildTypicalSimpleTable();

        Column expectedColumn = expectedDatabase.getTables().get(0).getColumns().get(0);
        expectedColumn.setType(DbType.FLOAT);

        Database actualDatabase = loadDatabase(sourceDatabase);
        Column actualColumn = actualDatabase.getTables().get(0).getColumns().get(0);
        Assert.assertNotEquals(actualColumn, expectedColumn, "This column should not be the same");
    }

    @Test
    public void testNegativeDifferentColumnNullable() throws Exception {
        Database sourceDatabase = buildTypicalSimpleTable();
        Database expectedDatabase = buildTypicalSimpleTable();

        Column expectedColumn = expectedDatabase.getTables().get(0).getColumns().get(0);
        expectedColumn.setNull(false);

        Database actualDatabase = loadDatabase(sourceDatabase);
        Column actualColumn = actualDatabase.getTables().get(0).getColumns().get(0);
        Assert.assertNotEquals(actualColumn, expectedColumn, "This column should not be the same");
    }

    @Test
    public void testNegativeDifferentCountOfColumns() throws Exception {
        Database sourceDatabase = buildTypicalSimpleTable();
        Database expectedDatabase = buildTypicalSimpleTable();

        Table expectedTable = new TableBuilder(expectedDatabase.getTables().get(0))
                                .addNotNullColumn("a new column", DbType.DATE)
                                .build();

        Database actualDatabase = loadDatabase(sourceDatabase);
        Table actualTable = actualDatabase.getTables().get(0);
        Assert.assertNotEquals(actualTable, expectedTable, "This table should not be the same");
    }



    private Database buildTypicalSimpleTable() {
        DatabaseBuilder sourceBuilder = new DatabaseBuilder();
        sourceBuilder.addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        return sourceBuilder.build();
    }
}
