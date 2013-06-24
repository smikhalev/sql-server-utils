package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public abstract class BaseExportStrategy implements ExportStrategy {
    protected String generateSelectClause(Table table) {
        return String.format("select %s ", table.getColumns().generateFields());
    }

    protected String generateFromClause(Table table) {
        return String.format("from %s ", table.getFullName());
    }
}
