package com.smikhalev.sqlserverutils.schema.dbobjects;

public class IndexColumn {
    private String name;
    private SortType sort;

    public IndexColumn(String name, SortType sort) {
        this.name = name;
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public SortType getSort() {
        return sort;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSort(SortType sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", name, sort.name().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        IndexColumn column = (IndexColumn) o;

        return name.equals(column.getName())
            && sort.equals(column.getSort());
    }
}
