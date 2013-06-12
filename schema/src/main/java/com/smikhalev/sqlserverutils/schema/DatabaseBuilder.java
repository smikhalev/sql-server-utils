package com.smikhalev.sqlserverutils.schema;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class DatabaseBuilder {

    private Database database = new Database();

    public DatabaseBuilder addTable(Table table)
    {
        database.getTables().put(table.getFullName(), table);
        return this;
    }

    public Database build()
    {
        return database;
    }
}
