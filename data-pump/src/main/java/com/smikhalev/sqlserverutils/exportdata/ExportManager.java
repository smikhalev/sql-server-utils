package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ExportManager {

    private Exporter exporter;
    private DatabaseLoader databaseLoader;

    public ExportManager(Exporter exporter, DatabaseLoader databaseLoader) {
        this.exporter = exporter;
        this.databaseLoader = databaseLoader;
    }

    public void doExport(String filePath) {
        Database database = databaseLoader.load();

        try(Writer writer = new FileWriter(filePath)){
            exporter.exportData(database, writer);
        }
        catch (IOException e){
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }
}
