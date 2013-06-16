package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.ParallelExportResultSetProcessor;

import java.util.concurrent.atomic.AtomicLong;

public class ExportWorker implements Runnable {
    private ParallelExportResultSetProcessor processor;
    private StatementExecutor executor;
    private String exportSelect;
    private AtomicLong overallExportedCount;

    public ExportWorker(ParallelExportResultSetProcessor processor, StatementExecutor executor, String exportSelect, AtomicLong overallExportedCount) {
        this.processor = processor;
        this.executor = executor;
        this.exportSelect = exportSelect;
        this.overallExportedCount = overallExportedCount;
    }

    @Override
    public void run() {
        executor.processResultSet(processor, exportSelect);
        overallExportedCount.addAndGet(processor.getLineExportedCount());
    }
}