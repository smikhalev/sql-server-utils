package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.ResultSetProcessor;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.ParallelExportResultSetProcessor;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.SequentialExportResultSetProcessor;

public class ExportWorker implements Runnable {
    private ResultSetProcessor processor;
    private StatementExecutor executor;
    private String exportSelect;

    public ExportWorker(ResultSetProcessor processor, StatementExecutor executor, String exportSelect){
        this.processor = processor;
        this.executor = executor;
        this.exportSelect = exportSelect;
    }

    @Override
    public void run() {
        executor.processResultSet(processor, exportSelect);
    }
}