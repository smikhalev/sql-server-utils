package com.smikhalev.sqlserverutils.schema;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public class Database implements Scriptable {
    private List<Table> tables = new ArrayList<>();

    public List<Table> getTables() {
        return tables;
    }

    @Override
    public String generateCreateScript() {
        ArrayList<String> tableCreateScripts = new ArrayList<>();

        for(Table table : tables) {
            tableCreateScripts.add(table.generateCreateScript());
        }

        return Joiner.on(NEW_LINE).join(tableCreateScripts);
    }

    @Override
    public String generateDropScript() {
        ArrayList<String> tableDropScripts = new ArrayList<>();

        for(Table table : tables) {
            tableDropScripts.add(table.generateDropScript());
        }

        return Joiner.on(NEW_LINE).join(tableDropScripts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Database database = (Database) o;

        return tables.equals(database.getTables());
    }

    @Override
    public String toString() {
        return generateCreateScript();
    }
}
