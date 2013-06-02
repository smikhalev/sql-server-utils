package com.smikhalev.sqlserverutils.schema.dbobjects;

public class ClusteredIndex extends Index {
    public ClusteredIndex(String name, Table table) {
        super(name, table);
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.CLUSTERED;
    }
}
