package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.BaseWorkerManager;
import com.smikhalev.sqlserverutils.TableReaderProvider;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.*;

public class ImportManager extends BaseWorkerManager {
    private final Importer importer;
    private long allRowsInFile = -1;

    public ImportManager(Importer importer, DatabaseLoader databaseLoader) {
        super(importer, databaseLoader);
        this.importer = importer;
    }

    public void doImport(String directoryPath) throws ApplicationException {
        initStartTime();
        Database database = getDatabaseLoader().load();
        allRowsInFile = -1;

        readStatistics(directoryPath);

        try {
            importer.importData(database, new FileSystemTableReaderProvider(directoryPath));
        }
        catch (Exception e) {
            fail();
            throw e;
        }
    }

    private void readStatistics(String directoryPath) {
        String statisticsFile = directoryPath + "\\" + "statistics.stats";
        try (BufferedReader reader = new BufferedReader(new FileReader(statisticsFile))) {
            String lineWithLinesCount = reader.readLine();
            allRowsInFile = Long.parseLong(lineWithLinesCount);
        }
        catch (IOException e) {
            fail();
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected long getAllRowsCount() {
        return allRowsInFile;
    }

    public class FileSystemTableReaderProvider implements TableReaderProvider {

        private String directory;

        public FileSystemTableReaderProvider(String directory) {
            this.directory = directory;
        }

        @Override
        public Reader provide(Table table) {
            //TODO; it should be updated since it is very possible that table name can't be use for file name
            String filePath = directory + "\\" + table.getSchema() + "_" + table.getName() + ".exportdata";

            try {
                return new FileReader(filePath);
            }
            catch (IOException e){
                throw new ApplicationException(e.getMessage(), e.getCause());
            }
        }
    }
}
