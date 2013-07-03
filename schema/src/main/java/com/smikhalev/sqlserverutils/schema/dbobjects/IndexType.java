package com.smikhalev.sqlserverutils.schema.dbobjects;

public enum IndexType {
    CLUSTERED("clustered"),
    NON_CLUSTERED("nonclustered");

    private final String name;

    IndexType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
