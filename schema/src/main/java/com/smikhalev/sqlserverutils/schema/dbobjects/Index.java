package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.exception.GenerateScriptException;

import java.util.ArrayList;
import java.util.List;

public abstract class Index extends DbObject {

    private Table table;
    private List<IndexColumn> keyColumns = new ArrayList<>();
    private boolean isUnique;

    public Index(String name, Table table, boolean isUnique) {
        super(name, null);
        this.table = table;
        this.isUnique = isUnique;
    }

    public Table getTable() {
        return table;
    }

    public List<IndexColumn> getKeyColumns() {
        return keyColumns;
    }

    public boolean isUnique(){
        return isUnique;
    }

    public abstract IndexType getIndexType();

    @Override
    public String generateCreateScript() {
        if (keyColumns.isEmpty())
            throw new GenerateScriptException("Index should contain at least one key column.");

        String keyColumnsScript = Joiner.on(Constants.COMMA_AND_NEW_LINE).join(keyColumns);
        String unique = isUnique ? "unique" : "";

        return String.format("create %s %s index [%s] on %s (%s)",
                unique, getIndexType().getName(), getName(), table.getFullName(), keyColumnsScript);
    }

    @Override
    public String generateDropScript() {
        return String.format("drop index [%s] on %s", getName(), table.getFullName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Index index = (Index) o;

        return super.equals(index)
            && keyColumns.equals(index.keyColumns)
            && isUnique == index.isUnique()
            && table.getFullName().equals(index.getTable().getFullName());
    }
}
