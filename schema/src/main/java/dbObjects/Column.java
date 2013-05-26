package dbObjects;

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

    @Override
    public String toString() {
        return String.format("[%s] [%s]%s null", name, type.getSqlType(), isNull ? "" : " not");
    }
}
