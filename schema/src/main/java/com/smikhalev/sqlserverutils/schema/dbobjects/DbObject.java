package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.google.common.base.Strings;
import com.smikhalev.sqlserverutils.schema.Scriptable;

/**
 *
 */
public abstract class DbObject implements Scriptable {

    private static final String DEFAULT_SCHEMA = "dbo";

    private String name;
    private String schema;

    public DbObject(String name, String schema) {
        this.name = name;
        this.schema = Strings.isNullOrEmpty(schema)
                ? DEFAULT_SCHEMA
                : schema;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getFullName() {
        return buildFullName(schema, name);
    }

    public static String buildFullName(String schema, String name) {
        return String.format("[%s].[%s]", schema, name);
    }

    @Override
    public boolean equals(Object o) {
        DbObject dbObject = (DbObject) o;

        return name.equals(dbObject.getName()) &&
               schema.equals(dbObject.getSchema());
    }
}
