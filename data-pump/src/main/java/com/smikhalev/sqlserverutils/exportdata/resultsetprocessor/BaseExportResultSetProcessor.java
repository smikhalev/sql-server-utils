package com.smikhalev.sqlserverutils.exportdata.resultsetprocessor;

import com.smikhalev.sqlserverutils.core.ResultSetProcessor;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutorException;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract class BaseExportResultSetProcessor implements ResultSetProcessor {
    private final Table table;
    private final Writer writer;
    private final ValueEncoder valueEncoder;
    private long lineExportedCount;

    public BaseExportResultSetProcessor(Table table, Writer writer, ValueEncoder valueEncoder){
        this.table = table;
        this.writer = writer;
        this.valueEncoder = valueEncoder;
    }

    protected abstract void writeNewLine() throws IOException;

    protected abstract void writeValue(String value) throws IOException;

    protected abstract void writeSeparator() throws IOException;

    protected Writer getWriter() {
        return writer;
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
            writeRowData(results, metaData);
            writeNewLine();
            lineExportedCount++;
        }
    }

    private void writeRowData(ResultSet results, ResultSetMetaData metaData) throws SQLException, IOException {
        for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            Object columnValue = results.getObject(columnIndex);
            DbType columnType = table.getColumns().get(columnIndex - 1).getType();

            String value = valueEncoder.encode(columnType, columnValue);
            writeValue(value);

            if (columnIndex != metaData.getColumnCount())
                writeSeparator();
        }
    }

    @Override
    public Object getResult() {
        return null;
    }

    public long getLineExportedCount() {
        return lineExportedCount;
    }
}
