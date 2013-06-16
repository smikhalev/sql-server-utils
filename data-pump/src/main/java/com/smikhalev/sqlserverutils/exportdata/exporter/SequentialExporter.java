package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.*;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.SequentialExportResultSetProcessor;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Writer;
import java.util.List;

public class SequentialExporter extends BaseExporter {
    public SequentialExporter(ExportStrategySelector exportStrategySelector,  ValueEncoder valueEncoder, StatementExecutor executor) {
        super(exportStrategySelector, valueEncoder, executor);
    }

    @Override
    protected void exportTable(List<String> selects, Table table, Writer writer) {
        SequentialExportResultSetProcessor processor = new SequentialExportResultSetProcessor(table, writer, getValueEncoder());

        for (String select : selects) {
            getExecutor().processResultSet(processor, select);
        }
    }
}

