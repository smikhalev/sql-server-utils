package com.smikhalev.sqlserverutils.test;

import com.google.common.collect.Lists;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategy;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategySelector;
import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.strategy.*;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

                    case "[dbo].[huge_table]":
                        return 100000;

                    default:
                        return 15;
                }
            }

            @Override
            public long getDatabaseSize() {
                return 1000;
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

        strategies.add(new TableSizeDependantStrategy(new FullExportStrategy(), tableSizeProvider, 10));
        strategies.add(new UniqueNonClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, 5));
        strategies.add(new UniqueClusteredIndexWithAnyNonClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, 5));
        strategies.add(new ClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, 5));
        strategies.add(new NonClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, 5));
        strategies.add(new FullExportStrategy());

        selector = new ExportStrategySelector(strategies);
    }

    @Test
    public void testExportOfSmallTableStrategy(){
        Table table = new TableBuilder("small_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof TableSizeDependantStrategy);
        ExportStrategy innerStrategy = ((TableSizeDependantStrategy) strategy).getInnerStrategy();
        Assert.assertTrue(innerStrategy instanceof FullExportStrategy);
    }

    @Test
    public void testUniqueNonClusteredIndexStrategy(){
        Table table = new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addUniqueNonClusteredIndex("unique_non_clustered", "bigint_column")
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof UniqueNonClusteredIndexChunkStrategy);
        List<String> queries = Lists.newArrayList(strategy.generateExportSelects(table).getExportSelects());
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
        List<String> queries = Lists.newArrayList(strategy.generateExportSelects(table).getExportSelects());
        Assert.assertTrue(!queries.isEmpty());
        Assert.assertTrue(queries.get(0).contains("order by [int_column]"));
    }

    @Test
    public void testUniqueClusteredIndexStrategyWithNonClustered(){
        Table table = new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .setUniqueClusteredIndex("clustered", "bigint_column")
                .addNonClusteredIndex("small_non_clustered", "bigint_column")
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof UniqueClusteredIndexWithAnyNonClusteredIndexChunkStrategy);
        List<String> queries = Lists.newArrayList(strategy.generateExportSelects(table).getExportSelects());
        Assert.assertTrue(!queries.isEmpty());
    }

    @Test
    public void testClusteredIndexStrategy(){
        Table table = new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .setClusteredIndex("clustered", "bigint_column")
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof ClusteredIndexChunkStrategy);
        List<String> queries = Lists.newArrayList(strategy.generateExportSelects(table).getExportSelects());
        Assert.assertTrue(!queries.isEmpty());
    }

    @Test
    public void testNonClusteredIndexStrategy(){
        Table table = new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addNonClusteredIndex("clustered", "bigint_column")
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof NonClusteredIndexChunkStrategy);
        List<String> queries = Lists.newArrayList(strategy.generateExportSelects(table).getExportSelects());
        Assert.assertTrue(!queries.isEmpty());
    }

    @Test
    public void testExportOfLastStrategy(){
        Table table = new TableBuilder("last_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .build();

        ExportStrategy strategy = selector.select(table);

        Assert.assertTrue(strategy instanceof FullExportStrategy);
    }

    @Test
    public void testPerformanceOfIndexChunk(){
        Table table = new TableBuilder("huge_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addNonClusteredIndex("non_clustered", "big_int_column")
                .build();

        ExportStrategy strategy = selector.select(table);
        strategy.generateExportSelects(table);
        Assert.assertTrue(strategy instanceof NonClusteredIndexChunkStrategy);
    }
}