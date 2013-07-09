package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

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

        List<ExportSelectData> exportSelects = new ArrayList<>();
        for (Table table : database.getTables()) {
            ExportStrategy exportStrategy = exportStrategySelector.select(table);
            ExportSelect exportSelect = exportStrategy.generateExportSelects(table);
            List<RestorableAction> restorableActions = exportStrategy.getRestorableActions(table);
            exportSelects.add(new ExportSelectData(exportSelect, restorableActions));
        }

        // First we start to export tables for which we don't need to create a copy tables
        // It gives us performance boost since copying is blocking operation in current implementation
        for (ExportSelectData exportSelect : exportSelects) {
            if (exportSelect.getRestorableActions().isEmpty()) {
                exportTable(exportSelect.getExportSelect(), writer);
            }
        }

        // Open restorable context for those tables where we need to create a copy
        try (RestorableContext context = new RestorableContext()) {
            for (ExportSelectData exportSelect : exportSelects) {
                if (!exportSelect.getRestorableActions().isEmpty()) {
                    context.prepare(exportSelect.getRestorableActions(), database);
                    exportTable(exportSelect.getExportSelect(), writer);
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

    private class ExportSelectData {
        private ExportSelect exportSelect;
        private List<RestorableAction> restorableActions;

        public ExportSelectData(ExportSelect exportSelect, List<RestorableAction> restorableActions) {
            this.exportSelect = exportSelect;
            this.restorableActions = restorableActions;
        }

        private ExportSelect getExportSelect() {
            return exportSelect;
        }

        private List<RestorableAction> getRestorableActions() {
            return restorableActions;
        }
    }
}
