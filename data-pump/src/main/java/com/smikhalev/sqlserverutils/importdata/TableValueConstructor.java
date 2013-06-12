package com.smikhalev.sqlserverutils.importdata;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.schema.dbobjects.Column;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public class TableValueConstructor {
    public String construct(Table table, List<List<String>> dataTable) {
        String valueScript = constructValues(dataTable);
        return String.format("select %s from (values %s) q(%s)",
                table.getColumns().generateFields(), valueScript, table.getColumns().generateFields());
    }

    private String constructValues(List<List<String>> dataTable) {
        List<String> values = new ArrayList<>();

        for(List<String> row : dataTable) {
            String rowValues = Joiner.on(",").join(row);
            values.add("(" + rowValues + ")");
        }

        return Joiner.on(",").join(values);
    }
}
