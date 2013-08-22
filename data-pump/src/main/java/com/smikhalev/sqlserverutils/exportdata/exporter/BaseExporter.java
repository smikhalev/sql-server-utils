package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.IOException;
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
    public void exportData(Database database, TableWriterProvider writerProvider) {
        startExport();

        List<Writer> writers = new ArrayList<>();

        try(RestorableContext context = new RestorableContext()) {
            for (Table table : database.getTables()) {
                ExportStrategy exportStrategy = exportStrategySelector.select(table);
                ExportSelect exportSelect = exportStrategy.generateExportSelects(table);

                List<RestorableAction> restorableActions = exportStrategy.getRestorableActions(table);
                context.prepare(restorableActions, database);

                Writer writer = writerProvider.provide(table);
                exportTable(exportSelect, writer);
                writers.add(writer);
            }

            finishExport();
        }
        finally {
            closeWriters(writers);
        }

        isFinished = true;
    }

    private void closeWriters(List<Writer> writers) {
        try {
            for(Writer writer : writers) {
                    writer.close();
            }
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
