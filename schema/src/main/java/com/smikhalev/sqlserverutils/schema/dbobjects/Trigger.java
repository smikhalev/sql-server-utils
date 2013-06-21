package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.smikhalev.sqlserverutils.core.Constants;

public class Trigger extends DbObject {

    private Table table;
    private String text;

    public Trigger(String name, String schema, Table table, String text) {
        super(name, schema);
        this.table = table;
        this.text = text;
    }

    @Override
    public String generateCreateScript() {
        return String.format("%screate trigger %s on %s after insert,delete,update as begin %s end",
                Constants.GO, getFullName(), table.getFullName(), text);
    }

    @Override
    public String generateDropScript() {
        return "drop trigger " + getFullName();
    }

    public String generateDisableScript() {
        return String.format("disable trigger %s on %s", getName(), table.getFullName());
    }

    public String generateEnableScript() {
        return String.format("enable trigger %s on %s", getName(), table.getFullName());
    }

    public Table getTable() {
        return table;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Trigger trigger = (Trigger) o;

        return super.equals(o)
            && table.getFullName().equals(trigger.getTable().getFullName());
    }
}
