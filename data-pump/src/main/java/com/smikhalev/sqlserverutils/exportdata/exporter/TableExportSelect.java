package com.smikhalev.sqlserverutils.exportdata.exporter;

import com.smikhalev.sqlserverutils.importdata.RestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public class TableExportSelect {
    private Table table;
    private List<String> exportSelects;
    private List<RestorableAction> restorableAction;

    public TableExportSelect(Table table, List<String> exportSelects) {
        this.table = table;
        this.exportSelects = exportSelects;
        this.restorableAction = new ArrayList<>();
    }

    public TableExportSelect(Table table, List<String> exportSelects, List<RestorableAction> restorableAction) {
        this.table = table;
        this.exportSelects = exportSelects;
        this.restorableAction = restorableAction;
    }

    public Table getTable() {
        return table;
    }

    public List<String> getExportSelects() {
        return exportSelects;
    }

    public List<RestorableAction> getRestorableAction(){
        return restorableAction;
    }
}
