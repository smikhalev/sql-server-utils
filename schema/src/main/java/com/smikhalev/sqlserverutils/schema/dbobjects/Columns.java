package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class Columns extends ArrayList<Column>  {

    private String fieldClause = null;

    public String generateFields() {
        if (fieldClause == null) {
            List<String> fields = new ArrayList<>();
            for(Column column : this) {
                fields.add(column.getName());
            }

            fieldClause = Joiner.on(",").join(fields);
        }

        return fieldClause;
    }

    public Column getByName(String name) {
        for(Column column : this) {
            if (column.getName().equals(name)) {
                return column;
            }
        }

        return null;
    }
}
