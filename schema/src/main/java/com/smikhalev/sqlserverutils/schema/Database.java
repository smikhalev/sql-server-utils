package com.smikhalev.sqlserverutils.schema;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.dbobjects.ForeignKey;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database implements Scriptable {
    private Map<String, Table> tables = new HashMap<>();

    public Map<String, Table> getTables() {
        return tables;
    }

    @Override
    public String generateCreateScript() {
        ArrayList<String> tableCreateScripts = new ArrayList<>();

        for(Table table : tables.values()) {
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

        for(Table table : tables.values()) {
            tableDropScripts.add(table.generateDropScript());
        }

        return Joiner.on(Constants.NEW_LINE).join(tableDropScripts);
    }

    private String generateDropForeignKeys() {
        List<String> dropForeignKeyScripts = new ArrayList<>();

        for (Table table : tables.values()) {
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
