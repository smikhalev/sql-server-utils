package com.smikhalev.sqlserverutils.schema;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;

public class DatabaseContext implements AutoCloseable {

    private StatementExecutor executor;
    private Database database;

    public DatabaseContext(Database database, StatementExecutor executor) {
        this.executor = executor;
        this.database = database;
    }

    @Override
    public void close() throws Exception {
        drop();
    }

    public void create() {
        String createScript = database.generateCreateScript();
        executor.executeScript(createScript);
    }

    public void drop() {
        String dropScript = database.generateDropScript();
        executor.executeScript(dropScript);
    }
}