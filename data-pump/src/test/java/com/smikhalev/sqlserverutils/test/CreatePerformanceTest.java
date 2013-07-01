package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
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
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class CreatePerformanceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private StatementExecutor executor;

    @Autowired
    private DataGenerator generator;

    private int TABLE_SIZE = 1 * 1000 * 1000;

    @Test(enabled = false)
    public void createPerformanceDatabase() throws Exception {
        Table clusteredTable = createTypicalTable("clustered_table")
                .setUniqueClusteredIndex("clustered_index", "int_column")
                .build();

        Table nonClusteredUniqueTable = createTypicalTable("non_clustered_unique_table")
                .addUniqueNonClusteredIndex("non_clustered_unique_index", "int_column")
                .addForeignKey("foreign_key_to_clustered_table", "int_column", clusteredTable)
                .build();

        Table heapTable = createTypicalTable("heap_table")
                .addEmptyTrigger("trigger_on_heap_table")
                .build();

        Database database = new DatabaseBuilder()
                .addTable(clusteredTable)
                .addTable(nonClusteredUniqueTable)
                .addTable(heapTable)
                .build();

        DatabaseContext dbContext = new DatabaseContext(database, executor);
        dbContext.create();
        generator.generateData(database, TABLE_SIZE);
    }

    private TableBuilder createTypicalTable(String tableName){
        return new TableBuilder(tableName)
                .addNullColumn("bit_column", DbType.BIT)
                .addNullColumn("int_column", DbType.INT)
                .addNullColumn("float_column", DbType.FLOAT)
                .addNullColumn("varchar_column", DbType.VARCHAR, 50)
                .addNullColumn("date_column", DbType.DATE);
    }
}
