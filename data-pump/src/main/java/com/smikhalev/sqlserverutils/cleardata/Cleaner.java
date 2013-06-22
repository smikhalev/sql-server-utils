package com.smikhalev.sqlserverutils.cleardata;

import com.smikhalev.sqlserverutils.schema.Database;

import java.io.Writer;

public interface Cleaner {
    public void clearData(Database database);
}
