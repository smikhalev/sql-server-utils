package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseExporter implements Exporter {
    private ExportStrategySelector exportStrategySelector;
    private ValueEncoder valueEncoder;
    private StatementExecutor executor;

    public BaseExporter(ExportStrategySelector exportStrategySelector, ValueEncoder valueEncoder, StatementExecutor executor) {
        this.exportStrategySelector = exportStrategySelector;
        this.valueEncoder = valueEncoder;
        this.executor = executor;
    }

    protected abstract void exportTable(List<String> selects, Table table, Writer writer);

    protected void finalExport() {
    };

    protected ValueEncoder getValueEncoder() {
        return valueEncoder;
    }

    protected StatementExecutor getExecutor() {
        return executor;
    }

    public void exportData(Database database, Writer writer) {
        for (Table table : database.getTables().values()) {
            ExportStrategy exportStrategy = exportStrategySelector.select(table);
            List<String> selects = exportStrategy.generateExportSelects(table);

            exportTable(selects, table, writer);
        }

        finalExport();
    }
}
