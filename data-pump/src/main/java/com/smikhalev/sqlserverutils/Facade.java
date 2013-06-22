package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.cleardata.CleanManager;
import com.smikhalev.sqlserverutils.exportdata.ExportManager;
import com.smikhalev.sqlserverutils.importdata.ImportManager;

public class Facade {
    private ExportManager exportManager;
    private ImportManager importManager;
    private CleanManager cleanManager;

    public Facade(ExportManager exportManager, ImportManager importManager, CleanManager cleanManager) {
        this.exportManager = exportManager;
        this.importManager = importManager;
        this.cleanManager = cleanManager;
    }

    public void doExport(String filePath) {
        exportManager.doExport(filePath);
    }

    public void doImport(String filePath) {
        importManager.doImport(filePath);
    }

    public void doClear() {
        cleanManager.doClear();
    }
}
