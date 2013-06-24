package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.ParallelExportResultSetProcessor;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategySelector;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Writer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelExporter extends BaseExporter {
    private ExecutorService threadPool;
    private AtomicLong overallExportedCount = new AtomicLong(0);

    public ParallelExporter(ExportStrategySelector exportStrategySelector, ValueEncoder valueEncoder, StatementExecutor executor, int threadCount) {
        super(exportStrategySelector, valueEncoder, executor);
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
    protected void finalExport() {
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
    }

    @Override
    public long getResult() {
        return overallExportedCount.longValue();
    }
}


