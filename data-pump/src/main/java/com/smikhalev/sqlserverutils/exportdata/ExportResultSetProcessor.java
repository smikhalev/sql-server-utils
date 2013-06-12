package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.core.ResultSetProcessor;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutorException;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ExportResultSetProcessor implements ResultSetProcessor {

    private Table table;
    private Writer writer;
    private ValueEncoder valueEncoder;

    public ExportResultSetProcessor(Table table, Writer writer, ValueEncoder valueEncoder){
        this.table = table;
        this.writer = writer;
        this.valueEncoder = valueEncoder;
    }

    @Override
    public void process(ResultSet resultSet) throws SQLException {
        try {
            writeResultSetBody(resultSet);
        } catch (SQLException | IOException e) {
            throw new StatementExecutorException(e.getMessage(), e);
        }
    }

    private void writeResultSetBody(ResultSet results) throws SQLException, IOException {
        ResultSetMetaData metaData = results.getMetaData();

        while (results.next()) {
            writeTableRowPrefix();
            writeRowData(results, metaData);
            writer.write(Constants.NEW_LINE);
        }
    }

    private void writeRowData(ResultSet results, ResultSetMetaData metaData) throws SQLException, IOException {
        for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            Object columnValue = results.getObject(columnIndex);
            DbType columnType = table.getColumns().get(columnIndex - 1).getType();

            String value = valueEncoder.encode(columnType, columnValue);
            writer.write(value);

            if (columnIndex != metaData.getColumnCount())
                writer.write(",");
        }
    }

    private void writeTableRowPrefix() throws SQLException, IOException {
        writer.write(table.getSchema());
        writer.write(",");

        writer.write(table.getName());
        writer.write(",");
    }

    @Override
    public Object getResult() {
        return null;
    }
}
