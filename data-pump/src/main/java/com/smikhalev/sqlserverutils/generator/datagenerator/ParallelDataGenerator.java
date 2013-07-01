package com.smikhalev.sqlserverutils.generator.datagenerator;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.exporter.ExportWorker;
import com.smikhalev.sqlserverutils.generator.ColumnGeneratorFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * This class can generate data in parallel using thread pool.
 * But it should be used only from one thread so it is not thread safe since it save state.
 * Also for dependant tables (with foreign keys you have to use sequential data generator)
  */
public class ParallelDataGenerator extends BaseDataGenerator {

    private int threadCount;
    private ExecutorService threadPool;

    public ParallelDataGenerator(ColumnGeneratorFactory columnGeneratorFactory, StatementExecutor executor, int chunkSize, int threadCount) {
        super(columnGeneratorFactory, executor, chunkSize);
        this.threadCount = threadCount;
    }

    @Override
    protected void startGenerateData() {
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


