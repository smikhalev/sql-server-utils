package com.smikhalev.sqlserverutils.core.connection;

import com.smikhalev.sqlserverutils.core.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionProvider implements ConnectionProvider {

    private String connectionString;

    public SimpleConnectionProvider(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }
}
