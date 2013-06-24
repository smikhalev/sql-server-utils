package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.BaseWorkerManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ImportManager extends BaseWorkerManager {
    private Importer importer;
    private long allRowsInFile = -1;

    public ImportManager(Importer importer, DatabaseLoader databaseLoader) {
        super(importer, databaseLoader);
        this.importer = importer;
    }

    public void doImport(String filePath) throws ApplicationException {
        Database database = getDatabaseLoader().load();
        allRowsInFile = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String lineWithLinesCount = reader.readLine();
            allRowsInFile = Long.parseLong(lineWithLinesCount);
            importer.importData(database, reader);

        } catch (IOException e) {
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected long getAllRowsCount() {
        return allRowsInFile;
    }
}
