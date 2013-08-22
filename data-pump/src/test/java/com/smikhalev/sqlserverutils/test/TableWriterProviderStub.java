package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.exportdata.TableWriterProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableWriterProviderStub implements TableWriterProvider {

    private Map<Table, StringWriter> writers = new HashMap<>();

    @Override
    public Writer provide(Table table) {
        StringWriter writer = new StringWriter();
        writers.put(table, writer);
        return writer;
    }

    public String getAggregateResult() {
        StringBuilder builder = new StringBuilder();
        for(StringWriter writer : writers.values()) {
            builder.append(writer.getBuffer().toString());
        }
        return builder.toString();
    }

    public Map<Table, StringWriter> getWriters() {
        return writers;
    }
}
