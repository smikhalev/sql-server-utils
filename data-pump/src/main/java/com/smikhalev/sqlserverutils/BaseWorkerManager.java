package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.schema.DatabaseLoader;


public abstract class BaseWorkerManager {
    private final Worker worker;
    private final DatabaseLoader databaseLoader;
    private long startTime;

    private boolean isFailed = false;

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

    protected void fail(){
        isFailed = true;
    }

    public ProcessResult getCurrentStatus() {
        long allRows = getAllRowsCount();
        long processedRows = worker.getResult();
        boolean isFinished = worker.isFinished();
        return new ProcessResult(allRows, processedRows, startTime, isFinished, isFailed);
    }
}
