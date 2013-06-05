package com.smikhalev.sqlserverutils.schema.dbobjects;

public class Column {

    private String name;
    private DbType type;
    private boolean isNull;

    public Column(String name, DbType type, boolean isNull) {
        this.name = name;
        this.type = type;
        this.isNull = isNull;
    }

    public DbType getType() {
        return type;
    }

    public boolean isNull() {
        return isNull;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(DbType type) {
        this.type = type;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s]%s null", name, type.getSqlType(), isNull ? "" : " not");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Column column = (Column) o;

        return isNull == column.isNull
            && name.equals(column.getName())
            && type == column.getType();
    }
}




