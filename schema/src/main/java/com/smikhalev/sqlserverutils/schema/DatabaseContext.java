package com.smikhalev.sqlserverutils.schema;

import com.smikhalev.sqlserverutils.core.executor.ScriptExecutor;

public class DatabaseContext implements AutoCloseable {

    private ScriptExecutor executor;
    private Database database;

    public DatabaseContext(Database database, ScriptExecutor executor) {
        this.executor = executor;
        this.database = database;
    }

    @Override
    public void close() throws Exception {
        drop();
    }

    public void create() {
        String createScript = database.generateCreateScript();
        executor.execute(createScript);
    }

    public void drop() {
        String dropScript = database.generateDropScript();
        executor.execute(dropScript);
    }
}