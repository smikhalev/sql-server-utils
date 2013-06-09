package com.smikhalev.sqlserverutils.export;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.schema.dbobjects.Column;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExportStrategy implements ExportStrategy {

    protected String generateSelectClause(Table table) {
        List<String> columnNames = new ArrayList<>();

        for(String columnName : table.getColumns().keySet()) {
            columnNames.add(String.format("[%s]", columnName));
        }

        String fields = Joiner.on(", ").join(columnNames);
        String select = String.format("select %s ", fields);
        return select;
    }

    protected String generateFromClause(Table table) {
        return String.format("from %s ", table.getFullName());
    }
}
