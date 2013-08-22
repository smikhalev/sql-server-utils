package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Writer;

public interface TableWriterProvider {
    public Writer provide(Table table);
}
