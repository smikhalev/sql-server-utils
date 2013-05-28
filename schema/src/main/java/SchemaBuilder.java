import dbObjects.Table;

public class SchemaBuilder {

    private Schema schema = new Schema();

    public SchemaBuilder addTable(Table table)
    {
        schema.getTables().add(table);
        return this;
    }

    public Schema build()
    {
        return schema;
    }
}
