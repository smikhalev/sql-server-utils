package com.smikhalev.sqlserverutils.export;

import com.smikhalev.sqlserverutils.schema.dbobjects.Index;

public interface IndexSizeProvider {
    public long getSize(Index index);
}
