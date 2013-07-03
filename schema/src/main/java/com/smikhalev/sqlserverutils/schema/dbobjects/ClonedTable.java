package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.smikhalev.sqlserverutils.core.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClonedTable extends Table {

    private final Table table;
    private final NonClusteredIndex nonClusteredIndex;

    private final static String ID_COLUMN_NAME = "__id__";
    private final static String TABLE_PREFIX = "__cloned_";

    public ClonedTable(Table table) {
        super(TABLE_PREFIX + table.getName(), table.getSchema());
        nonClusteredIndex = new NonClusteredIndex("unique_nonclustered_index", this, true);
        nonClusteredIndex.getKeyColumns().add(new IndexColumn(getIdColumn().getName(), SortType.ASC));
        this.table = table;
    }

    public Column getIdColumn() {
        return new Column(ID_COLUMN_NAME, DbType.VARCHAR, false);
    }

    public String getIdColumnScript() {
        return "newid() as " + ID_COLUMN_NAME;
    }

    @Override
    public boolean isHeap() {
        return true;
    }

    @Override
    public Columns getColumns() {
        return table.getColumns();
    }

    @Override
    public List<NonClusteredIndex> getNonClusteredIndexes() {
        return Arrays.asList(nonClusteredIndex);
    }

    @Override
    public List<ForeignKey> getForeignKeys() {
        return new ArrayList<>();
    }

    @Override
    public List<Trigger> getTriggers() {
        return new ArrayList<>();
    }

    @Override
    public String generateCreateScript() {
        String scriptTemplate = "select %s, %s into %s from %s";
        String createTable = String.format(scriptTemplate, getIdColumnScript(), table.getColumns().generateFields(), getFullName(), table.getFullName());

        String createIndex = getNonClusteredIndexes().get(0).generateCreateScript();

        return createTable + Constants.NEW_LINE + createIndex;
    }

    @Override
    public boolean equals(Object o) {
        return table.equals(o);
    }
}
