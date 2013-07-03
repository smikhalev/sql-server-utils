package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExportStrategy implements ExportStrategy {
    protected String generateSelectClause(Table table) {
        return String.format("select %s ", table.getColumns().generateFields());
    }

    protected String generateFromClause(Table table) {
        return String.format("from %s ", table.getFullName());
    }

    @Override
    public List<RestorableAction> getRestorableActions(Table table) {
        return new ArrayList<>();
    }
}
