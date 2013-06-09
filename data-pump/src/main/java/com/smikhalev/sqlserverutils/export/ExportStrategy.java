package com.smikhalev.sqlserverutils.export;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public interface ExportStrategy {
    public List<String> generateExportSelects(Table table);
    public boolean isApplicable(Table table);
}
