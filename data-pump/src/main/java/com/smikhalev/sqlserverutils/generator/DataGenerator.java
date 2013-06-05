package com.smikhalev.sqlserverutils.generator;

import com.smikhalev.sqlserverutils.schema.Database;

public interface DataGenerator {
    public void generateData(Database database, int count);
}
