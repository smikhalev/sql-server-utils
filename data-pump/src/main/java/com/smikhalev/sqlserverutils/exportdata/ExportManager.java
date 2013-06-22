package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.ProcessResult;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ExportManager {

    private Exporter exporter;
    private DatabaseLoader databaseLoader;
    private TableSizeProvider tableSizeProvider;

    public ExportManager(Exporter exporter, DatabaseLoader databaseLoader, TableSizeProvider tableSizeProvider) {
        this.exporter = exporter;
        this.databaseLoader = databaseLoader;
        this.tableSizeProvider = tableSizeProvider;
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

    public ProcessResult getCurrentStatus() {
        long allRows = tableSizeProvider.getDatabaseSize();
        long processedRows = exporter.getResult();
        return new ProcessResult(allRows, processedRows);
    }
}
