package com.smikhalev.sqlserverutils.core;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
    public Connection getConnection() throws SQLException;
    public void setConnectionString(String connectionString);
}
