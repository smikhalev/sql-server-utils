package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.exportdata.exporter.ExportSelect;
import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public interface ExportStrategy {
    public ExportSelect generateExportSelects(Table table);
    public boolean isApplicable(Table table);
    public List<RestorableAction> getRestorableActions(Table table);
}
