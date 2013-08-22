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

                String value = dataTable.get(i).get(j);
                if (value == null) {
                    stringBuilder.append("null");
                }
                else {
                    stringBuilder.append(value);
                }
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

            for (int i = 2; i < dataTable.get(0).size(); i++) {
                int valueLength = estimateValueLength(dataTable, i);
                rowSize += valueLength + COMMA_SIZE;
            }

            rowSize += BRACKET_SIZE;

            size = rowSize * dataTable.size();

            // This is average increment just to be sure that this size will be enough
            size += size / 15;
        }

        return size;
    }

    private int estimateValueLength(List<List<String>> dataTable, int i) {
        final int ROUGH_NULL_COLUMN_SIZE = 10;
        int valueLength;

        String value = dataTable.get(0).get(i);
        if (value == null) {
            // It is very rough estimate but for not char column should be fine in most cases
            valueLength = ROUGH_NULL_COLUMN_SIZE;
        }
        else {
            valueLength = value.length();
        }

        return valueLength;
    }
}
