package dbObjects;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class Table extends DbObject {

    private List<Column> columns = new ArrayList<Column>();

    public Table(String name) {
        super(name);
    }

    public Table(String name, String schema) {
        super(name, schema);
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public String generateCreateScript() {

        String columnsScript = Joiner.on(", ").join(columns);
        return String.format("create table %s (%s)", getFullName(), columnsScript);
    }

    @Override
    public String generateDropScript() {
        return String.format("drop table %s", getFullName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Table table = (Table) o;

        return super.equals(o) && columns.equals(table.getColumns());
    }
}
