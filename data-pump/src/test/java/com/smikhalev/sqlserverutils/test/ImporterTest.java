package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.Exporter;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.importdata.Importer;
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
    private Exporter exporter;

    @Autowired
    private DataGenerator generator;

    @Autowired
    private Importer importer;

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
            }
        }
    }

    private void renameDatabaseTables(Database database) {
        for(Table table : database.getTables().values()) {
            String newTableName = "renamed_" + table.getName();
            String renameScript = String.format("sp_rename %s, %s", table.getName(), newTableName);
            executor.executeScript(renameScript);
        }
    }

    private void addRenamedTablesToDatabase(Database database) {
        List<String> newTables = new ArrayList<>();

        for(Table table : database.getTables().values()) {
            String newTableName = "renamed_" + table.getName();
            newTables.add(newTableName);
        }

        for(String newTableName : newTables) {
            database.getTables().put(newTableName, new Table(newTableName, null));
        }
    }
}
