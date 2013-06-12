package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;

public interface ValueEncoder {
    public String encode(DbType type, Object value);
}
