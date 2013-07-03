package com.smikhalev.sqlserverutils.schema;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.dbobjects.ForeignKey;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.*;

public class Database implements Scriptable {
    private final List<Table> tables = new ArrayList<>();

    public List<Table> getTables() {
        return tables;
    }

    public Table getTableByFullName(String fullName) {
        for(Table table : tables) {
            if (table.getFullName().equals(fullName))
                return table;
        }

        return null;
    }

    @Override
    public String generateCreateScript() {
        List<String> tableCreateScripts = new LinkedList<>();

        for(Table table : tables) {
            tableCreateScripts.add(table.generateCreateScript());
        }

        return Joiner.on(Constants.NEW_LINE).join(tableCreateScripts);
    }

    @Override
    public String generateDropScript() {
        return generateDropForeignKeys() + Constants.NEW_LINE + generateDropTables();
    }

    private String generateDropTables() {
        ArrayList<String> tableDropScripts = new ArrayList<>();

        for(Table table : tables) {
            tableDropScripts.add(table.generateDropScript());
        }

        return Joiner.on(Constants.NEW_LINE).join(tableDropScripts);
    }

    private String generateDropForeignKeys() {
        List<String> dropForeignKeyScripts = new ArrayList<>();

        for (Table table : tables) {
            for (ForeignKey foreignKey : table.getForeignKeys()) {
                dropForeignKeyScripts.add(foreignKey.generateDropScript());
            }
        }

        return Joiner.on(Constants.NEW_LINE).join(dropForeignKeyScripts);
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
