import dbObjects.DbType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SchemaBuilderTest {

    private final String connectionString = "jdbc:jtds:sqlserver://mikhalevpc/mini-pump;user=sa;password=sa2013;";

    @Test
    public void testSimplestTable() throws Exception {
        SchemaBuilder builder = new SchemaBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Schema sourceSchema = builder.build();

        test(sourceSchema);
    }

    @Test (expectedExceptions = StatementExecutorException.class,
           expectedExceptionsMessageRegExp = "Column names in each table must be unique.*")
    public void testColumnsWithTheSameNameTable() throws Exception {
        SchemaBuilder builder = new SchemaBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Schema sourceSchema = builder.build();

        test(sourceSchema);
    }

    @Test
    public void testOneTableWithAllColumnTypes() throws Exception {
        SchemaBuilder builder = new SchemaBuilder();
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
        Schema sourceSchema = builder.build();

        test(sourceSchema);
    }

    @Test
    public void testSomeTablesTable() throws Exception {
        SchemaBuilder builder = new SchemaBuilder();
        builder.addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
               ).addTable(
                new TableBuilder("second_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Schema sourceSchema = builder.build();

        test(sourceSchema);
    }

    @Test (expectedExceptions = StatementExecutorException.class,
            expectedExceptionsMessageRegExp = "There is already an object named.*")
    public void testSomeTablesWithTheSameNameTable() throws Exception {
        SchemaBuilder builder = new SchemaBuilder();
        builder.addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
               ).addTable(
                new TableBuilder("first_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .build()
        );
        Schema sourceSchema = builder.build();

        test(sourceSchema);
    }

    private void test(Schema sourceSchema) throws Exception {
        try (SchemaSynchronizer ss = new SchemaSynchronizer(connectionString, sourceSchema)) {
            ss.create();
            SchemaLoader loader = new SchemaLoader(connectionString);
            Schema dbSchema = loader.load();
            Assert.assertEquals(dbSchema, sourceSchema, "Schema from database is not the same.");
        }
    }
}
