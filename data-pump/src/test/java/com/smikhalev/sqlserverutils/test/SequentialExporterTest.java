package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.exportdata.exporter.SequentialExporter;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SequentialExporterTest extends BaseExporterTest {

    @Autowired
    private SequentialExporter exporter;

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

        String exportString = exportDatabase(database, exporter, EXPORT_SIZE);

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

        String exportString = exportDatabase(database, exporter, EXPORT_SIZE);

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

        String exportString = exportDatabase(database, exporter, EXPORT_SIZE);

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

        String exportString = exportDatabase(database, exporter, EXPORT_SIZE);

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

        String exportString = exportDatabase(database, exporter, EXPORT_SIZE);

        Assert.assertNotNull(exportString);
        Assert.assertEquals(getExportStringSize(exportString), EXPORT_SIZE);
    }

}
