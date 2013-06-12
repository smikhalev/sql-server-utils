package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public interface TableSizeProvider {
    public long getSize(Table table);
}
