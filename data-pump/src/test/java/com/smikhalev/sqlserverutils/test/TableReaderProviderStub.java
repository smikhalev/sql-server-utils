package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.TableReaderProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class TableReaderProviderStub implements TableReaderProvider {

    private Map<Table, StringWriter> writers;

    public TableReaderProviderStub(Map<Table, StringWriter> writers) {
        this.writers = writers;
    }

    @Override
    public Reader provide(Table table) {
        StringWriter writer = writers.get(table);

        if (writer == null)
            return null;

        return new StringReader(writer.getBuffer().toString());
    }
}
