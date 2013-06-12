package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Index;

public interface IndexSizeProvider {
    public long getSize(Index index);
}
