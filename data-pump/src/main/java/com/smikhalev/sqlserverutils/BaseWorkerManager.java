package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

public abstract class BaseWorkerManager {
    private Worker worker;
    private DatabaseLoader databaseLoader;

    public BaseWorkerManager(Worker worker, DatabaseLoader databaseLoader) {
        this.worker = worker;
        this.databaseLoader = databaseLoader;
    }

    protected DatabaseLoader getDatabaseLoader() {
        return databaseLoader;
    }

    protected abstract long getAllRowsCount();

    public ProcessResult getCurrentStatus() {
        long allRows = getAllRowsCount();
        long processedRows = worker.getResult();
        return new ProcessResult(allRows, processedRows);
    }
}
