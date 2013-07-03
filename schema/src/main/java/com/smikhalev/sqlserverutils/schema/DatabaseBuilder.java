package com.smikhalev.sqlserverutils.schema;

import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class DatabaseBuilder {

    private final Database database = new Database();

    public DatabaseBuilder addTable(Table table)
    {
        database.getTables().add(table);
        return this;
    }

    public Database build()
    {
        return database;
    }
}
