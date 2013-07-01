package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.generator.ColumnGeneratorFactory;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.generator.datagenerator.SequentialDataGenerator;
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
public class IndexSizeProviderTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ColumnGeneratorFactory columnGeneratorFactory;

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private IndexSizeProvider indexSizeProvider;

    @Test
    public void testSimpleIndexSize() throws Exception {
        Database database = buildSimpleDatabase();

        long indexSize = exportDatabase(database);
        Assert.assertTrue(indexSize > 0);
    }

    private long exportDatabase(Database database) throws Exception {
        final int chunkSize = 100000;

        try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
            dbContext.create();

            DataGenerator generator = new SequentialDataGenerator(columnGeneratorFactory, executor, chunkSize);
            generator.generateData(database, 1230);

            return indexSizeProvider.getSize(database.getTableByFullName("[dbo].[simple_table]").getClusteredIndex());
        }
    }

    private Database buildSimpleDatabase() {
        return new DatabaseBuilder()
            .addTable(
                    new TableBuilder("simple_table")
                            .addNullColumn("bigint_column", DbType.BIGINT)
                            .setClusteredIndex("clustered", "bigint_column")
                            .build()
            ).build();
    }
}
