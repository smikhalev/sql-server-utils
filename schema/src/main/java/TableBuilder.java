import dbObjects.Column;
import dbObjects.DbType;
import dbObjects.Table;

public class TableBuilder {

    private Table table;

    public TableBuilder(String name, String schema) {
        table = new Table(name, schema);
    }

    public TableBuilder(String name) {
        table = new Table(name);
    }

    public TableBuilder addColumn(String name, DbType type, boolean isNull) {
        Column column = new Column(name, type, isNull);
        table.getColumns().add(column);
        return this;
    }

    public Table build() {
        return table;
    }
}
