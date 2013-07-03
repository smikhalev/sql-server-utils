package com.smikhalev.sqlserverutils.exportdata.resultsetprocessor;

import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.IOException;
import java.io.Writer;

/*
 * The main intent of this class is minimize synhronization time in base Writer.
 * That is why first of all we collect all values and write it in one action.
 */
public class ParallelExportResultSetProcessor extends BaseExportResultSetProcessor {

    private final StringBuffer line = new StringBuffer();

    public ParallelExportResultSetProcessor(Table table, Writer writer, ValueEncoder valueEncoder) {
        super(table, writer, valueEncoder);
    }

    @Override
    protected void writeValue(String value) throws IOException {
        line.append(value);
    }

    @Override
    protected void writeSeparator() throws IOException {
        line.append(",");
    }

    @Override
    protected void writeNewLine() throws IOException {
        line.append(Constants.NEW_LINE);
        getWriter().write(line.toString());
        line.setLength(0);
    }
}
