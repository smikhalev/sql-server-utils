package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategySelector;
import com.smikhalev.sqlserverutils.exportdata.Exporter;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.exportdata.exporter.ParallelExporter;
import com.smikhalev.sqlserverutils.exportdata.exporter.SequentialExporter;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ParallelExporterTest extends BaseExportTest {
    @Autowired
    private ExportStrategySelector selector;

    @Autowired
    private ValueEncoder valueEncoder;

    @Autowired
    private StatementExecutor executor;

    private final int EXPORT_SIZE = 10000;

    @Test   // 95,1 sec - chunk size = 10
    @Ignore // just to minimize build time
    public void testSimpleNotParallelExport() throws Exception {
        Exporter exporter = new SequentialExporter(selector, valueEncoder, executor);
        performanceTest(exporter);
    }

    @Test   // 99.3 sec - chunk size = 10
    @Ignore // just to minimize build time
    public void testSimpleParallelIn1ThreadExport() throws Exception {
        Exporter exporter = new ParallelExporter(selector, valueEncoder, executor, 1);
        performanceTest(exporter);
    }

    @Test   // 66,7 sec - chunk size = 10
    @Ignore // just to minimize build time
    public void testSimpleParallelIn2ThreadExport() throws Exception {
        Exporter exporter = new ParallelExporter(selector, valueEncoder, executor, 2);
        performanceTest(exporter);
    }

    @Test // 47,5 sec - chunk size = 10
    public void testSimpleParallelIn10ThreadExport() throws Exception {
        Exporter exporter = new ParallelExporter(selector, valueEncoder, executor, 10);
        performanceTest(exporter);
    }

    @Test // 45,09 sec - chunk size = 10
    @Ignore
    public void testSimpleParallelIn50ThreadExport() throws Exception {
        Exporter exporter = new ParallelExporter(selector, valueEncoder, executor, 50);
        performanceTest(exporter);
    }

    private void performanceTest(Exporter exporter) throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(
                        new TableBuilder("simple_table")
                                .addNullColumn("bit_column", DbType.BIT)
                                .addNullColumn("tyinyint_column", DbType.TINYINT)
                                .addNullColumn("smallint_column", DbType.SMALLINT)
                                .addNullColumn("int_column", DbType.INT)
                                .addNullColumn("bigint_column", DbType.BIGINT)
                                .addNullColumn("real_column", DbType.REAL)
                                .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                                .addNullColumn("varchar_column", DbType.VARCHAR, 25)
                                .addNullColumn("date_column", DbType.DATE)
                                .addNullColumn("time_column", DbType.TIME)
                                .addNullColumn("datetime_column", DbType.DATETIME)
                                .addNonClusteredIndex("non_clustered", "bigint_column")
                                .build()
                ).build();

        String exportString = exportDatabase(database, exporter, EXPORT_SIZE);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }
}
