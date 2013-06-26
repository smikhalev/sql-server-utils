package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public interface ImportStrategy {
    public String generateImportInsert(Packet packet);
    public boolean isApplicable(Table table);
}
