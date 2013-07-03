package com.smikhalev.sqlserverutils.core.connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.ConnectionProvider;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class C3P0ConnectionProvider implements ConnectionProvider {

    private ComboPooledDataSource connectionPool;
    private int minPoolSize;
    private int acquireIncrement;
    private int maxPoolSize;

    public C3P0ConnectionProvider(int minPoolSize, int acquireIncrement, int maxPoolSize){
        this.minPoolSize = minPoolSize;
        this.acquireIncrement = acquireIncrement;
        this.maxPoolSize = maxPoolSize;
    }

    public C3P0ConnectionProvider(String connectionString) {
        initConnectionPool(connectionString);
    }

    private void initConnectionPool(String connectionString) {
        connectionPool = new ComboPooledDataSource();
        try {
            connectionPool.setDriverClass("net.sourceforge.jtds.jdbcx.JtdsDataSource"); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
        connectionPool.setJdbcUrl(connectionString);

        connectionPool.setMinPoolSize(minPoolSize);
        connectionPool.setAcquireIncrement(acquireIncrement);
        connectionPool.setMaxPoolSize(maxPoolSize);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    @Override
    public void setConnectionString(String connectionString) {
        initConnectionPool(connectionString);
    }
}
