package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.Reader;

public interface TableReaderProvider {
    public Reader provide(Table table);
}
