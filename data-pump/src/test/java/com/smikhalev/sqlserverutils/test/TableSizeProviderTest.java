package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.generator.ColumnGeneratorFactory;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.generator.SimpleDataGenerator;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class TableSizeProviderTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private ColumnGeneratorFactory columnGeneratorFactory;

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private TableSizeProvider tableSizeProvider;

    @Test
    public void testSimpleTableSize() throws Exception {
        Database database = buildSimpleDatabase();

        int expectedTableSize = 10;
        long actualTableSize = exportDatabase(database, expectedTableSize);

        Assert.assertEquals(actualTableSize, expectedTableSize);
    }

    @Test
    public void testJustOneRowTableSize() throws Exception {
        Database database = buildSimpleDatabase();

        int expectedTableSize = 1;
        long actualTableSize = exportDatabase(database, expectedTableSize);

        Assert.assertEquals(actualTableSize, expectedTableSize);
    }

    @Test
    public void testMillionTableSize() throws Exception {
        Database database = buildSimpleDatabase();

        int expectedTableSize = 1000000;
        long actualTableSize = exportDatabase(database, expectedTableSize);

        Assert.assertEquals(actualTableSize, expectedTableSize);
    }

    private long exportDatabase(Database database, int tableSize) throws Exception {
        final int chunkSize = 100000;

        try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
            dbContext.create();

            DataGenerator generator = new SimpleDataGenerator(columnGeneratorFactory, executor, chunkSize);
            generator.generateData(database, tableSize);

            return tableSizeProvider.getSize(database.getTables().get("[dbo].[simple_table]"));
        }
    }

    private Database buildSimpleDatabase() {
        Database database = new DatabaseBuilder()
            .addTable(
                    new TableBuilder("simple_table")
                            .addNullColumn("bigint_column", DbType.BIGINT)
                            .build()
            ).build();
        return database;
    }
}
