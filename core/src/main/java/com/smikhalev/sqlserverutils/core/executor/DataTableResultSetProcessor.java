package com.smikhalev.sqlserverutils.core.executor;

import com.smikhalev.sqlserverutils.core.ResultSetProcessor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataTableResultSetProcessor implements ResultSetProcessor<DataTable> {

    private final DataTable dataTable = new DataTable();

    @Override
    public void process(ResultSet resultSet) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();

        while (resultSet.next()) {
            DataRow row = createRow(resultSet, metaData);
            dataTable.getRows().add(row);
        }
    }

    @Override
    public DataTable getResult() {
        return dataTable;
    }

    private DataRow createRow(ResultSet results, ResultSetMetaData metaData) throws SQLException{
        DataRow row = new DataRow();

        for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            String columnName = metaData.getColumnName(columnIndex);
            Object columnValue = results.getObject(columnIndex);
            row.put(columnName, columnValue);
        }

        return row;
    }
}
