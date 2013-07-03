package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

public abstract class BaseWorkerManager {
    private final Worker worker;
    private final DatabaseLoader databaseLoader;
    private long startTime;

    protected BaseWorkerManager(Worker worker, DatabaseLoader databaseLoader) {
        this.worker = worker;
        this.databaseLoader = databaseLoader;
    }

    protected void initStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    protected DatabaseLoader getDatabaseLoader() {
        return databaseLoader;
    }

    protected abstract long getAllRowsCount();

    public ProcessResult getCurrentStatus() {
        long allRows = getAllRowsCount();
        long processedRows = worker.getResult();
        boolean isFinished = worker.isFinished();
        return new ProcessResult(allRows, processedRows, startTime, isFinished);
    }
}
