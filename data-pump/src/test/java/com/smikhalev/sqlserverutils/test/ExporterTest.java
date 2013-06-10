package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.export.Exporter;
import com.smikhalev.sqlserverutils.export.ExportStrategySelector;
import com.smikhalev.sqlserverutils.export.ValueEncoder;
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

import java.io.StringWriter;
import java.io.Writer;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class ExporterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ColumnGeneratorFactory columnGeneratorFactory;

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private ExportStrategySelector strategySelector;

    @Autowired
    private ValueEncoder valueEncoder;

    private final int EXPORT_SIZE = 123;

    @Test
    public void testSimpleExport() throws Exception {
        Database database = new DatabaseBuilder()
            .addTable(
                    new TableBuilder("simple_table")
                            .addNullColumn("bigint_column", DbType.BIGINT)
                            .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                            .build()
            ).build();

        String exportString = exportDatabase(database);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }

    @Test
    public void testUniqueNonClusteredIndexExport() throws Exception {
        Database database = new DatabaseBuilder()
            .addTable(new TableBuilder("middle_table")
                .addNullColumn("bigint_column", DbType.BIGINT)
                .addNullColumn("int_column", DbType.INT)
                .addNullColumn("bit_column", DbType.BIT)
                .addUniqueNonClusteredIndex("small_unique_non_clustered", "int_column")
                .addUniqueNonClusteredIndex("unique_non_clustered", "bigint_column")
                .addNonClusteredIndex("small_non_clustered", "bit_column")
                .build()
            ).build();

        String exportString = exportDatabase(database);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }

    @Test
    public void testUniqueClusteredIndexWithNonClusteredExport() throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(new TableBuilder("middle_table")
                        .addNullColumn("bigint_column", DbType.BIGINT)
                        .addNullColumn("int_column", DbType.INT)
                        .addNullColumn("bit_column", DbType.BIT)
                        .setUniqueClusteredIndex("unique_clustered", "bigint_column")
                        .addNonClusteredIndex("small_non_clustered", "bit_column")
                        .build()
                ).build();

        String exportString = exportDatabase(database);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }

    @Test
    public void testClusteredIndexExport() throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(new TableBuilder("middle_table")
                        .addNullColumn("bigint_column", DbType.BIGINT)
                        .addNullColumn("int_column", DbType.INT)
                        .addNullColumn("bit_column", DbType.BIT)
                        .setClusteredIndex("clustered", "bigint_column")
                        .build()
                ).build();

        String exportString = exportDatabase(database);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }

    @Test
    public void testNonClusteredIndexExport() throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(new TableBuilder("middle_table")
                        .addNullColumn("bigint_column", DbType.BIGINT)
                        .addNullColumn("int_column", DbType.INT)
                        .addNullColumn("bit_column", DbType.BIT)
                        .addNonClusteredIndex("small_non_clustered", "bigint_column")
                        .build()
                ).build();

        String exportString = exportDatabase(database);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }

    private int getExportStringSize(String export) {
        return export.length() - export.replace("\n", "").length();
    }

    private String exportDatabase(Database database) throws Exception {
        String result;

        final int chunkSize = 100;

        try(Writer writer = new StringWriter()) {
            Exporter exporter = new Exporter(strategySelector, valueEncoder, executor);

            try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
                dbContext.create();

                DataGenerator generator = new SimpleDataGenerator(columnGeneratorFactory, executor, chunkSize);
                generator.generateData(database, EXPORT_SIZE);

                exporter.export(database, writer);
            }
            result = ((StringWriter) writer).getBuffer().toString();
        }

        return result;
    }
}
