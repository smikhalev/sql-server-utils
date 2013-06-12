package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Writer;
import java.util.List;

public class Exporter {
    private ExportStrategySelector exportStrategySelector;
    private StatementExecutor executor;
    private ValueEncoder valueEncoder;

    public Exporter(ExportStrategySelector exportStrategySelector, ValueEncoder valueEncoder, StatementExecutor executor) {
        this.exportStrategySelector = exportStrategySelector;
        this.executor = executor;
        this.valueEncoder = valueEncoder;
    }

    public void exportData(Database database, Writer writer) {
        for (Table table : database.getTables().values()) {
            exportTable(table, writer);
        }
    }

    private void exportTable(Table table, Writer writer) {
        ExportStrategy exportStrategy = exportStrategySelector.select(table);
        List<String> selects = exportStrategy.generateExportSelects(table);
        ExportResultSetProcessor processor = new ExportResultSetProcessor(table, writer, valueEncoder);

        for (String select : selects) {
            executor.processResultSet(processor, select);
        }
    }
}

