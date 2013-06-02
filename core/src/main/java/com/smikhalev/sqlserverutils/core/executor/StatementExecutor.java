package com.smikhalev.sqlserverutils.core.executor;

import com.smikhalev.sqlserverutils.core.ConnectionProvider;

public abstract class StatementExecutor {

    private ConnectionProvider connectionProvider;

    static {
        initSqlServerDriver();
    }

    public StatementExecutor(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    protected ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    private static void initSqlServerDriver() {
        try {
            Class.forName("net.sourceforge.jtds.jdbcx.JtdsDataSource");
        } catch (ClassNotFoundException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }
}
