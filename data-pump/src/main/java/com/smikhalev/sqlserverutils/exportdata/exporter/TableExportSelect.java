package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class TableExportSelect {
    private Table table;
    private List<String> exportSelects;

    public TableExportSelect(Table table, List<String> exportSelects) {
        this.table = table;
        this.exportSelects = exportSelects;
    }

    public Table getTable() {
        return table;
    }

    public List<String> getExportSelects() {
        return exportSelects;
    }
}
