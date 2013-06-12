package com.smikhalev.sqlserverutils.exportdata;

import com.google.common.base.Joiner;
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
}
