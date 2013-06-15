package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.exporter.SequentialExporter;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.importdata.Importer;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class ImporterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private SequentialExporter exporter;

    @Autowired
    private DataGenerator generator;

    @Autowired
    private Importer importer;

    @Autowired
    private TableComparator comparator;

    private final int EXPORT_SIZE = 123;

    @Test
    public void testSimpleImport() throws Exception {
        Database database = new DatabaseBuilder()
                .addTable(
                        new TableBuilder("simple_table")
                                .addNullColumn("bigint_column", DbType.BIGINT)
                                .addNullColumn("nvarchar_column", DbType.NVARCHAR, 20)
                                .build()
                ).build();

        importDatabase(database);
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

        importDatabase(database);
    }

    private void importDatabase(Database database) throws Exception {
        String result;

        try(Writer writer = new StringWriter()) {
            try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
                dbContext.create();

                generator.generateData(database, EXPORT_SIZE);

                exporter.exportData(database, writer);

                result = ((StringWriter) writer).getBuffer().toString();

                StringReader reader = new StringReader(result);

                renameDatabaseTables(database);
                dbContext.create();
                addRenamedTablesToDatabase(database);

                importer.importData(database, reader);
                compareImportTables(database);
            }
        }
    }

    private void compareImportTables(Database database) {
        for(Table table : database.getTables().values()) {
            if (!table.getName().startsWith("renamed")) {
                String key = DbObject.buildFullName(table.getSchema(), buildRenamedTableName(table));
                Table targetTable = database.getTables().get(key);
                Assert.assertNotNull(targetTable);

                DataTable compareResult = comparator.compare(table, targetTable);

                if (!compareResult.getRows().isEmpty()) {
                    printDataTable(compareResult);
                }

                Assert.assertTrue(compareResult.getRows().isEmpty());
            }
        }
    }

    private void printDataTable(DataTable compareResult) {
        for (DataRow row : compareResult.getRows()) {
            for(Object value : row.values()) {
                System.out.print(value);
                System.out.print(",");
            }
            System.out.println();
        }
    }

    private void renameDatabaseTables(Database database) {
        for(Table table : database.getTables().values()) {
            String newTableName = buildRenamedTableName(table);
            String renameScript = String.format("sp_rename %s, %s", table.getName(), newTableName);
            executor.executeScript(renameScript);
        }
    }

    private void addRenamedTablesToDatabase(Database database) {
        List<String> newTables = new ArrayList<>();

        for(Table table : database.getTables().values()) {
            String newTableName = buildRenamedTableName(table);
            newTables.add(newTableName);
        }

        for(String newTableName : newTables) {
            Table table = new Table(newTableName, null);
            database.getTables().put(table.getFullName(), table);
        }
    }

    private String buildRenamedTableName(Table table) {
        return "renamed_" + table.getName();
    }
}
