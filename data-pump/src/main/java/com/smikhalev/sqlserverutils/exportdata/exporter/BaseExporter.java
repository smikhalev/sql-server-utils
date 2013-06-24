package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.RestorableContext;
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

    protected abstract void exportTable(TableExportSelect tableExportSelect, Writer writer);

    protected void finalExport() {
    }

    protected ValueEncoder getValueEncoder() {
        return valueEncoder;
    }

    protected StatementExecutor getExecutor() {
        return executor;
    }

    @Override
    public void exportData(Database database, Writer writer) {
        List<TableExportSelect> tableExportSelects = new ArrayList<>();
        for (Table table : database.getTables().values()) {
            ExportStrategy exportStrategy = exportStrategySelector.select(table);
            tableExportSelects.add(exportStrategy.generateExportSelects(table));
        }

        // First we start to export tables for which we don't need to create a copy tables
        for (TableExportSelect tableExportSelect : tableExportSelects) {
            if (tableExportSelect.getRestorableAction().isEmpty()) {
                exportTable(tableExportSelect, writer);
            }
        }

        // Open restorable context for those tables where we need to create a copy
        try (RestorableContext context = new RestorableContext()) {
            for (TableExportSelect tableExportSelect : tableExportSelects) {
                if (!tableExportSelect.getRestorableAction().isEmpty()) {
                    context.prepare(tableExportSelect.getRestorableAction(), database);
                    exportTable(tableExportSelect, writer);
                }
            }

            finalExport();
        }
    }
}
