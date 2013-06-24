package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.RestorableContext;
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
    protected void exportTable(TableExportSelect tableExportSelect, Writer writer) {
        SequentialExportResultSetProcessor processor = new SequentialExportResultSetProcessor(tableExportSelect.getTable(), writer, getValueEncoder());

        for (String select : tableExportSelect.getExportSelects()) {
            getExecutor().processResultSet(processor, select);
        }

        processResult = processor.getLineExportedCount();
    }

    @Override
    public long getResult() {
        return processResult;
    }
}

