package dbObjects;

/**
 *
 */
public abstract class DbObject implements Scriptable {

    private static final String DEFAULT_SCHEMA = "dbo";

    private String name;
    private String schema;

    public DbObject(String name) {
        this(name, DEFAULT_SCHEMA);
    }

    public DbObject(String name, String schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public String getFullName() {
        return String.format("[%s].[%s]", schema, name);
    }

    @Override
    public boolean equals(Object o) {
        DbObject dbObject = (DbObject) o;

        return name.equals(dbObject.getName()) &&
               schema.equals(dbObject.getSchema());
    }
}
