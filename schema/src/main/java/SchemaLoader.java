import dbObjects.Column;
import dbObjects.DbType;
import dbObjects.Table;

import java.util.List;

public class SchemaLoader {
    //TODO: should be extracted in DI
    private DataTableQueryExecutor queryExecutor;

    public SchemaLoader(String connectionString) {
        queryExecutor = new DataTableQueryExecutor(connectionString);
    }

    public Schema load() {
        Schema schema = new Schema();

        loadTables(schema.getTables());

        return schema;
    }

    public void loadTables(List<Table> tables) {
        String query =
            "select" +
            "    object_id," +
            "    t.name as table_name," +
            "    s.name as schema_name " +
            "from sys.tables t " +
            "inner join sys.schemas s" +
            "    on t.schema_id = s.schema_id";

        DataTable dataTable = queryExecutor.execute(query);

        for (DataRow row : dataTable.getRows()) {
            int objectId = (int) row.get("object_id");
            String tableName = (String) row.get("table_name");
            String schemaName = (String) row.get("schema_name");

            Table table = new Table(tableName, schemaName);

            loadColumns(objectId, table.getColumns());

            tables.add(table);
        }
    }

    public void loadColumns(int objectId, List<Column> columns) {
        String query =
            "select" +
            "    c.name as column_name," +
            "    t.name as type_name," +
            "    c.is_nullable " +
            "from sys.columns c " +
            "inner join sys.types t" +
            "    on c.user_type_id = t.user_type_id " +
            "where object_id = ?";

        DataTable dataTable = queryExecutor.execute(query, objectId);

        for (DataRow row : dataTable.getRows()) {
            String columnName = (String) row.get("column_name");
            String typeName = (String) row.get("type_name");
            boolean isNull = (boolean) row.get("is_nullable");

            DbType dbType = DbType.valueOf(typeName.toUpperCase());
            Column column = new Column(columnName, dbType, isNull);

            columns.add(column);
        }
    }
}
