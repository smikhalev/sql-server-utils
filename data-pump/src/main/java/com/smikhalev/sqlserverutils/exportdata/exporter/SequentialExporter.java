package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.SequentialExportResultSetProcessor;

import java.io.Writer;

public class SequentialExporter extends BaseExporter {
    private long processResult;

    public SequentialExporter(ExportStrategySelector exportStrategySelector,  ValueEncoder valueEncoder, StatementExecutor executor) {
        super(exportStrategySelector, valueEncoder, executor);
    }

    @Override
    protected void exportTable(ExportSelect exportSelect, Writer writer) {
        SequentialExportResultSetProcessor processor = new SequentialExportResultSetProcessor(exportSelect.getTable(), writer, getValueEncoder());

        for (String select : exportSelect.getExportSelects()) {
            getExecutor().processResultSet(processor, select);
        }

        processResult = processor.getLineExportedCount();
    }

    @Override
    public long getResult() {
        return processResult;
    }
}

