package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.importdata.Importer;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;
import com.smikhalev.sqlserverutils.importdata.importer.ImportContext;
import com.smikhalev.sqlserverutils.importdata.importer.ParallelImporter;
import com.smikhalev.sqlserverutils.importdata.importer.SequentialImporter;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class ParallelImporterTest extends BaseImporterTest {

    @Autowired
    private PacketImporter packetImporter;


    @Test
    public void testSimpleParallelIn1ThreadImport() throws Exception {
        Importer importer = new ParallelImporter(packetImporter, 10, 1);
        test(importer);
    }

    @Test
    public void testSimpleParallelIn10ThreadImport() throws Exception {
        Importer importer = new ParallelImporter(packetImporter, 10, 10);
        test(importer);
    }

    @Test
    public void testSimpleNonParallelImport() throws Exception {
        Importer importer = new SequentialImporter(packetImporter, 10);
        test(importer);
    }

    private void test(Importer importer) throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(
                        new TableBuilder("simple_table")
                                .addNullColumn("bigint_column", DbType.BIGINT)
                                .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                                .build()
                ).build();

        importDatabase(database, importer, 123);
    }
}