package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public interface ImportStrategy {
    public String generateImportInsert(Packet packet);
    public boolean isApplicable(Table table);
    public List<RestorableAction> getRestorableAction(Table table);
    public int getSize();
}
