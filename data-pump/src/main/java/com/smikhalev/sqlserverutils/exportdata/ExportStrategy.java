package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.exportdata.exporter.TableExportSelect;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public interface ExportStrategy {
    public TableExportSelect generateExportSelects(Table table);
    public boolean isApplicable(Table table);
}
