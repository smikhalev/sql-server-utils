package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.importdata.importer.SequentialImporter;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class ImporterTest extends BaseImporterTest {

    @Autowired
    private SequentialImporter importer;

    @Test
    public void testSimpleImport() throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(
                        new TableBuilder("simple_table")
                                .addNullColumn("bigint_column", DbType.BIGINT)
                                .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                                .build()
                ).build();

        importDatabase(database, importer, 123);
    }

    @Test
    public void testAllColumnTypesImport() throws Exception {
        Database database = new DatabaseBuilder()
            .addTable(
                    new TableBuilder("simple_table")
                            .addNullColumn("bit_column", DbType.BIT)
                            .addNullColumn("tyinyint_column", DbType.TINYINT)
                            .addNullColumn("smallint_column", DbType.SMALLINT)
                            .addNullColumn("int_column", DbType.INT)
                            .addNullColumn("bigint_column", DbType.BIGINT)
                            //.addNullColumn("float_column", DbType.FLOAT)
                            .addNullColumn("real_column", DbType.REAL)
                            .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                            .addNullColumn("varchar_column", DbType.VARCHAR, 25)
                            .addNullColumn("date_column", DbType.DATE)
                            .addNullColumn("time_column", DbType.TIME)
                            .addNullColumn("datetime_column", DbType.DATETIME)
                            .build()
            ).build();

        importDatabase(database, importer, 123);
    }

    @Test
    public void testImportWithForeignKey() throws Exception {
        Table mainTable = new TableBuilder("main_table")
                .addNotNullColumn("id", DbType.INT)
                .addUniqueNonClusteredIndex("unique", "id")
                .build();
        Table dependantTable = new TableBuilder("dependant_table")
                .addNotNullColumn("id", DbType.INT)
                .addForeignKey("fk_main_ref", "id", mainTable)
                .build();
        Database database = new DatabaseBuilder()
                .addTable(mainTable)
                .addTable(dependantTable)
                .build();

        importDatabase(database, importer, 123);
    }
}
