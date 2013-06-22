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

    @Test//(enabled = false) 154 sec
    public void createPerformanceDatabase() throws Exception {
        Table nonClusteredUniqueTable = createTypicalTable("non_clustered_unique_table")
                .addUniqueNonClusteredIndex("non_clustered_unique_index", "nvarchar_column")
                .build();

        Table nonClusteredTable = createTypicalTable("non_clustered_table")
                .addNonClusteredIndex("non_clustered_index", "varchar_column")
                .build();

        Table clusteredUniqueTable = createTypicalTable("clustered_unique_table")
                .setUniqueClusteredIndex("clustered_unique_index", "nvarchar_column")
                .build();

        Table clusteredTable = createTypicalTable("clustered_table")
                .setClusteredIndex("clustered_index", "bigint_column")
                .build();

        Database database = new DatabaseBuilder()
                .addTable(clusteredTable)
                .addTable(clusteredUniqueTable)
                .addTable(nonClusteredTable)
                .addTable(nonClusteredUniqueTable)
                .build();

        DatabaseContext dbContext = new DatabaseContext(database, executor);
        dbContext.create();
        generator.generateData(database, TABLE_SIZE);
    }

    private TableBuilder createTypicalTable(String tableName){
        return new TableBuilder(tableName)
                .addNullColumn("bit_column", DbType.BIT)
                .addNullColumn("tyinyint_column", DbType.TINYINT)
                .addNullColumn("smallint_column", DbType.SMALLINT)
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addNullColumn("float_column", DbType.FLOAT)
                .addNullColumn("real_column", DbType.REAL)
                .addNullColumn("nvarchar_column", DbType.NVARCHAR, 50)
                .addNullColumn("varchar_column", DbType.VARCHAR, 25)
                .addNullColumn("date_column", DbType.DATE)
                .addNullColumn("time_column", DbType.TIME)
                .addNullColumn("datetime_column", DbType.DATETIME);
    }
}
