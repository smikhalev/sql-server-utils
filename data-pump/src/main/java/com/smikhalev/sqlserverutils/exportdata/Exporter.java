package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.Worker;
import com.smikhalev.sqlserverutils.schema.Database;

import java.io.Writer;

public interface Exporter extends Worker {
    public void exportData(Database database, Writer writer);
}
