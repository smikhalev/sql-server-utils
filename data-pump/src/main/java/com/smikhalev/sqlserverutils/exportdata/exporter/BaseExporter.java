package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import javafx.util.Pair;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseExporter implements Exporter {
    private final ExportStrategySelector exportStrategySelector;
    private final ValueEncoder valueEncoder;
    private final StatementExecutor executor;
    private boolean isFinished = false;

    public BaseExporter(ExportStrategySelector exportStrategySelector, ValueEncoder valueEncoder, StatementExecutor executor) {
        this.exportStrategySelector = exportStrategySelector;
        this.valueEncoder = valueEncoder;
        this.executor = executor;
    }

    protected abstract void exportTable(ExportSelect exportSelect, Writer writer);

    protected void startExport() {}

    protected void finishExport() {}

    protected ValueEncoder getValueEncoder() {
        return valueEncoder;
    }

    protected StatementExecutor getExecutor() {
        return executor;
    }

    @Override
    public void exportData(Database database, Writer writer) {
        startExport();

        List<Pair<ExportSelect, List<RestorableAction>>> pairs = new ArrayList<>();
        for (Table table : database.getTables()) {
            ExportStrategy exportStrategy = exportStrategySelector.select(table);
            ExportSelect exportSelect = exportStrategy.generateExportSelects(table);
            List<RestorableAction> restorableActions = exportStrategy.getRestorableActions(table);
            pairs.add(new Pair(exportSelect, restorableActions));
        }

        // First we start to export tables for which we don't need to create a copy tables
        // It gives us performance boost since copying is blocking operation in current implementation
        for (Pair<ExportSelect, List<RestorableAction>> pair : pairs) {
            if (pair.getValue().isEmpty()) {
                exportTable(pair.getKey(), writer);
            }
        }

        // Open restorable context for those tables where we need to create a copy
        try (RestorableContext context = new RestorableContext()) {
            for (Pair<ExportSelect, List<RestorableAction>> pair : pairs) {
                if (!pair.getValue().isEmpty()) {
                    context.prepare(pair.getValue(), database);
                    exportTable(pair.getKey(), writer);
                }
            }

            finishExport();
        }

        isFinished = true;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
