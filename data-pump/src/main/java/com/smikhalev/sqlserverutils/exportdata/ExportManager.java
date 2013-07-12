package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.BaseWorkerManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ExportManager extends BaseWorkerManager {
    private final Exporter exporter;
    private final TableSizeProvider tableSizeProvider;
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
            String linesCountLine = Long.toString(getAllRowsCount()) + Constants.NEW_LINE;
            writer.write(linesCountLine);
            exporter.exportData(database, writer);
        }
        catch (IOException e){
            fail();
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
        catch (Exception e) {
            fail();
            throw e;
        }
    }

    @Override
    protected long getAllRowsCount() {
        if (allRowsCount == -1)
            allRowsCount = tableSizeProvider.getDatabaseSize();

        return allRowsCount;
    }
}
