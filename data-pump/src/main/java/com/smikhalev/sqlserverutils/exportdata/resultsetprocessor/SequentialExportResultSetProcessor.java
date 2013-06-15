package com.smikhalev.sqlserverutils.exportdata.resultsetprocessor;

import com.smikhalev.sqlserverutils.core.Constants;
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

/*
 * This class is not good for parallel execution and should be used only in sequential mode.
 */
public class SequentialExportResultSetProcessor extends BaseExportResultSetProcessor {

    public SequentialExportResultSetProcessor(Table table, Writer writer, ValueEncoder valueEncoder){
        super(table, writer, valueEncoder);
    }

    @Override
    protected void writeNewLine() throws IOException {
        getWriter().write(Constants.NEW_LINE);
    }

    @Override
    protected void writeValue(String value) throws IOException {
        getWriter().write(value);
    }

    @Override
    protected void writeSeparator() throws IOException {
        getWriter().write(",");
    }
}
