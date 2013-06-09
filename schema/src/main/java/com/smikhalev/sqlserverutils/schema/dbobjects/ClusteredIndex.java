package com.smikhalev.sqlserverutils.schema.dbobjects;

public class ClusteredIndex extends Index {
    public ClusteredIndex(String name, Table table, boolean isUnique) {
        super(name, table, isUnique);
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.CLUSTERED;
    }
}
