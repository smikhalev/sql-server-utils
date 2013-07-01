package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.BaseWorkerManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ExportManager extends BaseWorkerManager {
    private Exporter exporter;
    private TableSizeProvider tableSizeProvider;
    private long allRowsCount = -1;

    public ExportManager(Exporter exporter, DatabaseLoader databaseLoader, TableSizeProvider tableSizeProvider) {
        super(exporter, databaseLoader);
        this.exporter = exporter;
        this.tableSizeProvider = tableSizeProvider;
    }

    public void doExport(String filePath) {
        initStartTime();
        Database database = getDatabaseLoader().load();
        allRowsCount = -1;

        try(Writer writer = new FileWriter(filePath)){
            String linesCountLine = new Long(getAllRowsCount()).toString() + Constants.NEW_LINE;
            writer.write(linesCountLine);
            exporter.exportData(database, writer);
        }
        catch (IOException e){
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected long getAllRowsCount() {
        if (allRowsCount == -1)
            allRowsCount = tableSizeProvider.getDatabaseSize();

        return allRowsCount;
    }
}
