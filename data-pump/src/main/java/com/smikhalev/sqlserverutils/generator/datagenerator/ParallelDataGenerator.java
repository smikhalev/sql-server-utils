package com.smikhalev.sqlserverutils.generator.datagenerator;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.exporter.ExportWorker;
import com.smikhalev.sqlserverutils.generator.ColumnGeneratorFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelDataGenerator extends BaseDataGenerator {

    private ExecutorService threadPool;

    public ParallelDataGenerator(ColumnGeneratorFactory columnGeneratorFactory, StatementExecutor executor, int chunkSize, int threadCount) {
        super(columnGeneratorFactory, executor, chunkSize);
        threadPool = Executors.newFixedThreadPool(threadCount);

    }

    @Override
    protected void generatePartData(final String generateScript) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                getExecutor().executeScript(generateScript);
            }
        });
    }

    @Override
    protected void finishGenerateData() {
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
    }
}


