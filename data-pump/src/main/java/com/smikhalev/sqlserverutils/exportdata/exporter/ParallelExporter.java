package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.resultsetprocessor.ParallelExportResultSetProcessor;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategySelector;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;

import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelExporter extends BaseExporter {
    private ExecutorService threadPool;

    public ParallelExporter(ExportStrategySelector exportStrategySelector, ValueEncoder valueEncoder, StatementExecutor executor, int threadCount) {
        super(exportStrategySelector, valueEncoder, executor);
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void doExport(Iterable<TableExportSelect> exportSelects, Writer writer) {
        for(TableExportSelect exportSelect : exportSelects) {
            for (String select : exportSelect.getExportSelects()) {
                ParallelExportResultSetProcessor processor = new ParallelExportResultSetProcessor(exportSelect.getTable(), writer, getValueEncoder());
                Runnable exportWorker = new ExportWorker(processor, getExecutor(), select);
                threadPool.execute(exportWorker);
            }
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {}
    }
}
