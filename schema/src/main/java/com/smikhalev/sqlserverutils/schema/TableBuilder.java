package com.smikhalev.sqlserverutils.schema;

import com.google.common.collect.Lists;
import com.smikhalev.sqlserverutils.schema.dbobjects.*;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

    private Table table;

    public TableBuilder(String name) {
        table = new Table(name, null);
    }

    public TableBuilder(Table table) {
        this.table = table;
    }

    public TableBuilder addNullColumn(String name, DbType type) {
        return addColumn(name, type, true);
    }

    public TableBuilder addNullColumn(String name, DbType type, int size) {
        return addColumn(name, type, true, size);
    }

    public TableBuilder addNotNullColumn(String name, DbType type) {
        return addColumn(name, type, false);
    }

    public TableBuilder addNotNullColumn(String name, DbType type, int size) {
        return addColumn(name, type, false, size);
    }

    public TableBuilder setClusteredIndex(String name, String... columns) {
        ClusteredIndex index = new ClusteredIndex(name, table);
        List<IndexColumn> indexColumns = convertIntoDefaultIndexKeyColumn(columns);
        index.getKeyColumns().addAll(indexColumns);
        table.setClusteredIndex(index);

        return this;
    }

    public TableBuilder addNonClusteredIndex(String name, String... columns) {
        NonClusteredIndex index = new NonClusteredIndex(name, table);
        List<IndexColumn> indexColumns = convertIntoDefaultIndexKeyColumn(columns);
        index.getKeyColumns().addAll(indexColumns);
        table.getNonClusteredIndexes().add(index);

        return this;
    }

    public TableBuilder addNonClusteredIndexWithIncludeColumns(String name, String[]  keyColumns, String[] includedColumns) {
        NonClusteredIndex index = new NonClusteredIndex(name, table);
        List<IndexColumn> indexColumns = convertIntoDefaultIndexKeyColumn(keyColumns);
        index.getKeyColumns().addAll(indexColumns);
        index.getIncludedColumns().addAll(Lists.newArrayList(includedColumns));

        table.getNonClusteredIndexes().add(index);

        return this;
    }


    private List<IndexColumn> convertIntoDefaultIndexKeyColumn(String[] columns) {
        List<IndexColumn> indexColumns = new ArrayList<>();

        for (String column : columns) {
            indexColumns.add(new IndexColumn(column, SortType.ASC));
        }

        return indexColumns;
    }

    private TableBuilder addColumn(String name, DbType type, boolean isNull, int size) {
        Column column = new CharColumn(name, type, isNull, size);
        table.getColumns().add(column);
        return this;
    }

    private TableBuilder addColumn(String name, DbType type, boolean isNull) {
        if (type == DbType.NVARCHAR || type == DbType.VARCHAR)
            return addColumn(name, type, isNull, 1);

        Column column = new Column(name, type, isNull);
        table.getColumns().add(column);
        return this;
    }

    public Table build() {
        return table;
    }
}
