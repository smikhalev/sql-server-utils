package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public class Packet {

    private Table table;
    private long uniqueId;
    private List<List<String>> dataTable = new ArrayList<>();
    private int size;

    public Packet(Table table, long uniqueId, int size) {
        this.table = table;
        this.uniqueId = uniqueId;
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

    public long getUniqueId(){
        return uniqueId;
    }

    public int size() {
        return size;
    }

    public boolean isFull() {
        return dataTable.size() == size;
    }
}
