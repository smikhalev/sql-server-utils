package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.export.ExportStrategy;
import com.smikhalev.sqlserverutils.export.ExportStrategySelector;
import com.smikhalev.sqlserverutils.export.IndexSizeProvider;
import com.smikhalev.sqlserverutils.export.TableSizeProvider;
import com.smikhalev.sqlserverutils.export.strategy.FullExportOfSmallTableStrategy;
import com.smikhalev.sqlserverutils.export.strategy.FullExportStrategy;
import com.smikhalev.sqlserverutils.export.strategy.UniqueNonClusteredIndexChunkStrategy;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class StrategySelectorTest extends AbstractTestNGSpringContextTests {

    private ExportStrategySelector selector;

    @BeforeTest
    private void setUp() {
        List<ExportStrategy> strategies = new ArrayList<>();

        TableSizeProvider tableSizeProvider = new TableSizeProvider() {
            @Override
            public long getSize(Table table) {
                switch (table.getFullName()) {
                    case "[dbo].[small_table]":
                        return 5;

                    default:
                        return 15;
                }
            }
        };

        IndexSizeProvider indexSizeProvider = new IndexSizeProvider() {
            @Override
            public long getSize(Index index) {
                switch (index.getFullName()) {
                    case "[dbo].[small_unique_non_clustered]":
                        return 5;

                    case "[dbo].[unique_non_clustered]":
                        return 10;

                    case "[dbo].[big_unique_non_clustered]":
                        return 15;

                    case "[dbo].[small_non_clustered]":
                        return 3;

                    default:
                        return 10;
                }
            }
        };

        strategies.add(new FullExportOfSmallTableStrategy(tableSizeProvider, 10));
        strategies.add(new UniqueNonClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, 5));
        strategies.add(new FullExportStrategy());

        selector = new ExportStrategySelector(strategies);
    }

    @Test
    public void testExportOfSmallTableStrategy(){
        Table table = new TableBuilder("small_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof FullExportOfSmallTableStrategy);
    }

    @Test
    public void testUniqueNonClusteredIndexStrategy(){
        Table table = new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addUniqueNonClusteredIndex("unique_non_clustered", "bigint_column")
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof UniqueNonClusteredIndexChunkStrategy);
        List<String> queries = strategy.generateExportSelects(table);
        Assert.assertTrue(!queries.isEmpty());
    }

    @Test
    public void testMinUniqueNonClusteredIndexStrategy(){
        Table table = new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addUniqueNonClusteredIndex("small_unique_non_clustered", "int_column")
                .addUniqueNonClusteredIndex("unique_non_clustered", "bigint_column")
                .addNonClusteredIndex("small_non_clustered", "bit_column")
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof UniqueNonClusteredIndexChunkStrategy);
        List<String> queries = strategy.generateExportSelects(table);
        Assert.assertTrue(!queries.isEmpty());
        Assert.assertTrue(queries.get(0).contains("order by [int_column]"));
    }

    @Test
    public void testExportOfLastStrategy(){
        Table table = new TableBuilder("last_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof FullExportStrategy);
    }
}