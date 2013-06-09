package com.smikhalev.sqlserverutils.export;

import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public interface TableSizeProvider {
    public long getSize(Table table);
}
