package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.BaseWorkerManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

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

    public void doExport(String directoryPath) {
        initStartTime();
        Database database = getDatabaseLoader().load();
        allRowsCount = -1;

        writeStatistics(directoryPath);

        try {
            TableWriterProvider tableWriterProvider = new FileSystemTableWriterProvider(directoryPath);
            exporter.exportData(database, tableWriterProvider);
        }
        catch (Exception e){
            fail();
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }

    private void writeStatistics(String directoryPath) {
        String statisticsFile = directoryPath + "\\" + "statistics.stats";
        try(Writer writer = new FileWriter(statisticsFile)){
            String linesCountLine = Long.toString(getAllRowsCount()) + Constants.NEW_LINE;
            writer.write(linesCountLine);
        }
        catch (IOException e){
            fail();
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected long getAllRowsCount() {
        if (allRowsCount == -1)
            allRowsCount = tableSizeProvider.getDatabaseSize();

        return allRowsCount;
    }

    public class FileSystemTableWriterProvider implements TableWriterProvider {

        private String directory;

        public FileSystemTableWriterProvider(String directory) {
            this.directory = directory;
        }

        @Override
        public Writer provide(Table table)  {
            //TODO; it should be updated since it is very possible that table name can't be use for file name
            String filePath = directory + "\\" + table.getSchema() + "_" + table.getName() + ".exportdata";

            try {
                return new FileWriter(filePath);
            }
            catch (IOException e){
                throw new ApplicationException(e.getMessage(), e.getCause());
            }
        }
    }
}
