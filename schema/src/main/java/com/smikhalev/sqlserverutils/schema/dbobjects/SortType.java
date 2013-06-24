package com.smikhalev.sqlserverutils.schema.dbobjects;

public enum SortType {
    DESC,
    ASC;

    public static SortType reverseSort(SortType sortType) {
        if (sortType == DESC)
            return ASC;

        return DESC;
    }
}
