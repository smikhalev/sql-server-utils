package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.*;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class ImportContextTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private DatabaseLoader databaseLoader;

    @Autowired
    private RestorableContext restorableContext;

    @Test
    public void testImportContextDisableForeignKeys() throws Exception {
        Table mainTable = new TableBuilder("main_table")
                .addNotNullColumn("id", DbType.INT)
                .addUniqueNonClusteredIndex("unique", "id")
                .build();
        Table dependantTable = new TableBuilder("dependant_table")
                .addNotNullColumn("id", DbType.INT)
                .addForeignKey("fk_main_ref", "id", mainTable)
                .build();
        Database expectedDatabase = new DatabaseBuilder()
                .addTable(mainTable)
                .addTable(dependantTable)
                .build();

        try (DatabaseContext dbContext = new DatabaseContext(expectedDatabase, executor)) {
            dbContext.create();
            Database databaseWithForeignKeys = databaseLoader.load();

            Table table = databaseWithForeignKeys.getTables().get("[dbo].[dependant_table]");
            Assert.assertNotNull(table);
            Assert.assertTrue(!table.getForeignKeys().isEmpty());

            restorableContext.prepare(databaseWithForeignKeys);

            Database databaseWithoutForeignKeys = databaseLoader.load();
            table = databaseWithoutForeignKeys.getTables().get("[dbo].[dependant_table]");
            Assert.assertNotNull(table);
            Assert.assertTrue(table.getForeignKeys().isEmpty());

            restorableContext.restore();

            Database actualDatabase = databaseLoader.load();
            Assert.assertEquals(actualDatabase, expectedDatabase);
        }
    }
}
