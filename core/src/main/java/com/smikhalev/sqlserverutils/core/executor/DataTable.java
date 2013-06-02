package com.smikhalev.sqlserverutils.core.executor;

import java.util.ArrayList;
import java.util.List;

public class DataTable {
    private List<DataRow> rows = new ArrayList<>();

    public List<DataRow> getRows() {
        return rows;
    }
}
