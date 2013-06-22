package com.smikhalev.sqlserverutils.cleardata;

import com.smikhalev.sqlserverutils.schema.Database;

public interface Cleaner {
    public void clearData(Database database);
}
