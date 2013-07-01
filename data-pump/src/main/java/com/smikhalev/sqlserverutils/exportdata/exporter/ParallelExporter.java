package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.ParallelExportResultSetProcessor;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategySelector;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;

import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelExporter extends BaseExporter {
    private int threadCount;
    private ExecutorService threadPool;
    private AtomicLong overallExportedCount = new AtomicLong(0);

    public ParallelExporter(ExportStrategySelector exportStrategySelector, ValueEncoder valueEncoder, StatementExecutor executor, int threadCount) {
        super(exportStrategySelector, valueEncoder, executor);
        this.threadCount = threadCount;
    }

    @Override
    protected void startExport() {
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    protected void exportTable(TableExportSelect tableExportSelect, Writer writer) {
        for (String select : tableExportSelect.getExportSelects()) {
            ParallelExportResultSetProcessor processor = new ParallelExportResultSetProcessor(tableExportSelect.getTable(), writer, getValueEncoder());
            Runnable exportWorker = new ExportWorker(processor, getExecutor(), select, overallExportedCount);
            threadPool.execute(exportWorker);
        }
    }

    @Override
    protected void finishExport() {
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
    }

    @Override
    public long getResult() {
        return overallExportedCount.longValue();
    }
}


