package com.smikhalev.sqlserverutils.cleardata;

import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

public class CleanManager {

    private Cleaner cleaner;
    private DatabaseLoader databaseLoader;

    public CleanManager(Cleaner cleaner, DatabaseLoader databaseLoader) {
        this.cleaner = cleaner;
        this.databaseLoader = databaseLoader;
    }

    public void doClear() {
        Database database = databaseLoader.load();
        cleaner.clearData(database);
    }
}
