package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.SequentialExportResultSetProcessor;

import java.io.Writer;

public class SequentialExporter extends BaseExporter {
    public SequentialExporter(ExportStrategySelector exportStrategySelector,  ValueEncoder valueEncoder, StatementExecutor executor) {
        super(exportStrategySelector, valueEncoder, executor);
    }

    @Override
    public void doExport(Iterable<TableExportSelect> exportSelects, Writer writer) {
        for(TableExportSelect exportSelect : exportSelects) {
            SequentialExportResultSetProcessor processor = new SequentialExportResultSetProcessor(exportSelect.getTable(), writer, getValueEncoder());

            for (String select : exportSelect.getExportSelects()) {
                getExecutor().processResultSet(processor, select);
            }
        }
    }
}

