package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseLoader;

import java.io.*;

public class ImportManager {

    private Importer importer;
    private DatabaseLoader databaseLoader;

    public ImportManager(Importer importer, DatabaseLoader databaseLoader) {
        this.importer = importer;
        this.databaseLoader = databaseLoader;
    }

    public void doImport(String filePath) throws ApplicationException {
        Database database = databaseLoader.load();

        try(Reader reader = new FileReader(filePath)){
            importer.importData(database, reader);
        }
        catch (IOException e){
            throw new ApplicationException(e.getMessage(), e.getCause());
        }
    }
}
