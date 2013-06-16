package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.Exporter;
import com.smikhalev.sqlserverutils.exportdata.exporter.ParallelExporter;
import com.smikhalev.sqlserverutils.generator.DataGenerator;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.io.StringWriter;
import java.io.Writer;

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class BaseExporterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StatementExecutor executor;

    @Autowired
    private DataGenerator generator;

    protected String exportDatabase(Database database, Exporter exporter, int exportSize) throws Exception {
        String result;

        try(Writer writer = new StringWriter()) {
            try (DatabaseContext dbContext = new DatabaseContext(database, executor)) {
                dbContext.create();

                generator.generateData(database, exportSize);

                exporter.exportData(database, writer);
            }
            result = ((StringWriter) writer).getBuffer().toString();
        }

        return result;
    }

    protected int getExportStringSize(String export) {
        return export.length() - export.replace("\n", "").length();
    }
}
