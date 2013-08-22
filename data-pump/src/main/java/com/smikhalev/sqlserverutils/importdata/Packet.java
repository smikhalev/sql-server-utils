package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public class Packet {

    private final Table table;
    private final List<List<String>> dataTable = new ArrayList<>();
    private final int size;

    public Packet(Table table, int size) {
        this.table = table;
        this.size = size;
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

    public int size() {
        return dataTable.size();
    }

    public boolean isFull() {
        return dataTable.size() == size;
    }

    public boolean isEmpty() {
        return dataTable.size() == 0;
    }
}
