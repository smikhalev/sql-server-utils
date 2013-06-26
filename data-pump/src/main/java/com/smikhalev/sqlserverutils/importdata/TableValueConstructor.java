package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class TableValueConstructor {

    public String construct(Table table, List<List<String>> dataTable) {
        int stringBuilderEstimateLength = estimateLength(dataTable);
        StringBuilder stringBuilder = new StringBuilder(stringBuilderEstimateLength);

        stringBuilder.append("select ");
        stringBuilder.append(table.getColumns().generateFields());
        stringBuilder.append(" from (values ");
        appendValues(dataTable, stringBuilder);
        stringBuilder.append(") q( ");
        stringBuilder.append(table.getColumns().generateFields());
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private void appendValues(List<List<String>> dataTable, StringBuilder stringBuilder) {
        for(int i = 0; i < dataTable.size(); i++) {
            if (i != 0)
                stringBuilder.append(",");

            stringBuilder.append("(");
            for(int j = 0; j < dataTable.get(i).size(); j++) {
                if (j != 0)
                    stringBuilder.append(",");

                stringBuilder.append(dataTable.get(i).get(j));
            }
            stringBuilder.append(")");
        }
    }

    private int estimateLength(List<List<String>> dataTable) {
        int size = 0;

        if (dataTable.size() > 0 && dataTable.get(0).size() > 0) {
            int rowSize = 0;

            final int COMMA_SIZE = 1;
            final int BRACKET_SIZE = 2;

            for (String value : dataTable.get(0)) {
                rowSize += value.length() + COMMA_SIZE;
            }

            rowSize += BRACKET_SIZE;

            size = rowSize * dataTable.size();

            // This is average increment just to be sure that this size will be enough
            size += size / 15;
        }

        return size;
    }


}
