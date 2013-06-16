package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.exporter.SequentialExporter;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.importdata.Importer;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class BaseImporterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private SequentialExporter exporter;

    @Autowired
    private DataGenerator generator;

    @Autowired
    private TableComparator comparator;

    protected void importDatabase(Database database, Importer importer, int size) throws Exception {
        String result;

        try(Writer writer = new StringWriter()) {
            try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
                dbContext.create();

                generator.generateData(database, size);

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
}
