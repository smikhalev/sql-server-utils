package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.Database;

import java.io.Writer;

public interface Exporter {
    public void exportData(Database database, Writer writer);
}
