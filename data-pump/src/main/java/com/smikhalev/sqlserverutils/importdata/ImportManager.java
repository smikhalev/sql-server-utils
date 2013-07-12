package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.BaseWorkerManager;
import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ImportManager extends BaseWorkerManager {
    private final Importer importer;
    private long allRowsInFile = -1;

    public ImportManager(Importer importer, DatabaseLoader databaseLoader) {
        super(importer, databaseLoader);
        this.importer = importer;
    }

    public void doImport(String filePath) throws ApplicationException {
        initStartTime();
        Database database = getDatabaseLoader().load();
        allRowsInFile = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String lineWithLinesCount = reader.readLine();
            allRowsInFile = Long.parseLong(lineWithLinesCount);
            importer.importData(database, reader);

        } catch (IOException e) {
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
        return allRowsInFile;
    }
}
