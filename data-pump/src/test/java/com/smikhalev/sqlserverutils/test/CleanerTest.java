package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.cleardata.Cleaner;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class CleanerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private DataGenerator generator;

    @Autowired
    private Cleaner cleaner;

    @Autowired
    private TableSizeProvider tableSizeProvider;

    @Test
    public void testClearOneTable() throws Exception {
        Database database = new DatabaseBuilder()
            .addTable(
                    new TableBuilder("simple_table")
                            .addNullColumn("bigint_column", DbType.BIGINT)
                            .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                            .build()
            ).build();

        testClear(database, 100);
    }

    @Test
    public void testClearSeveralTable() throws Exception {
        Database database = new DatabaseBuilder()
            .addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bigint_column", DbType.BIGINT)
                        .build())
            .addTable(
                new TableBuilder("simple_table2")
                        .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                        .build()
            )
        .build();

        testClear(database, 100);
    }

    @Test
    public void testClearWithForeignKeysTable() throws Exception {
        Table mainTable = new TableBuilder("main_table")
            .addNotNullColumn("id", DbType.INT)
            .addUniqueNonClusteredIndex("unique", "id")
            .build();
        Table dependantTable = new TableBuilder("dependant_table")
            .addNotNullColumn("id", DbType.INT)
            .addForeignKey("fk_main_ref", "id", mainTable)
            .build();
        Database database = new DatabaseBuilder()
            .addTable(mainTable)
            .addTable(dependantTable)
            .build();

        testClear(database, 100);
    }

    protected void testClear(Database database, int size) throws Exception {
        try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
            dbContext.create();

            generator.generateData(database, size);

            cleaner.clearData(database);

            assertAllTableAreEmpty(database);
        }
    }

    private void assertAllTableAreEmpty(Database database) {
        for (Table table: database.getTables()) {
            long size = tableSizeProvider.getSize(table);

            Assert.assertEquals(size, 0);
        }
    }
}
