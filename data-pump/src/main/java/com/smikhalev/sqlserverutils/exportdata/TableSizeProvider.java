package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public interface TableSizeProvider {
    public int getSize(Table table);
    public long getDatabaseSize();
}
