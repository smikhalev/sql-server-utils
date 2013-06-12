package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public class Packet {

    private Table table;
    private List<List<String>> dataTable = new ArrayList<>();

    public Packet(Table table) {
        this.table = table;
    }

    public void add(List<String> row) {
        dataTable.add(row);
    }

    public List<List<String>> getDataTable() {
        return dataTable;
    }

    public Table getTable() {
        return table;
    }

    public void clear() {
        dataTable.clear();
    }

    public int size() {
        return dataTable.size();
    }
}
