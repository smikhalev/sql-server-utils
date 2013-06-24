package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.Worker;
import com.smikhalev.sqlserverutils.schema.Database;

import java.io.Reader;

public interface Importer extends Worker {
    public void importData(Database database, Reader reader);
}
