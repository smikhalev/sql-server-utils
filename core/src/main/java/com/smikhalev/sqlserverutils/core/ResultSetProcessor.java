package com.smikhalev.sqlserverutils.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor <TResult> {
    void process(ResultSet resultSet) throws SQLException;
    TResult getResult();
}
