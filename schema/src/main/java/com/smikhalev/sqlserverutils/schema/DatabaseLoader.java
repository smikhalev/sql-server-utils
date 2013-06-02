package com.smikhalev.sqlserverutils.schema;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.DataTableQueryExecutor;
import com.smikhalev.sqlserverutils.schema.dbobjects.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DatabaseLoader {

    private DataTableQueryExecutor queryExecutor;

    @Autowired
    public DatabaseLoader(DataTableQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public Database load() {
        Database database = new Database();

        loadTables(database.getTables());

        return database;
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

            loadColumns(objectId, table);
            loadIndexes(objectId, table);

            tables.add(table);
        }
    }

    public void loadColumns(int objectId, Table table) {
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

            table.getColumns().add(column);
        }
    }

    public void loadIndexes(int objectId, Table table) {
        String query =
            "select name, index_id, type\n" +
            "from sys.indexes\n" +
            "where object_id = ?" +
            " and type <> 0";

        DataTable dataTable = queryExecutor.execute(query, objectId);

        for (DataRow row : dataTable.getRows()) {
            String indexName = (String) row.get("name");
            int indexId = (int) row.get("index_id");
            int indexTypeId = (int) row.get("type");

            IndexType indexType = IndexType.values()[indexTypeId - 1];

            Index index = indexType == IndexType.CLUSTERED
                    ? new ClusteredIndex(indexName, table)
                    : new NonClusteredIndex(indexName, table);

            loadIndex(objectId, indexId, index);

            if (indexType == IndexType.CLUSTERED)
                table.setClusteredIndex((ClusteredIndex) index);
            else
                table.getNonClusteredIndexes().add((NonClusteredIndex) index);
        }
    }

    private void loadIndex(int objectId, int indexId, Index index) {
        String query =
            "select " +
            "    c.name as column_name,\n" +
            "    ic.is_descending_key,\n" +
            "    ic.is_included_column\n" +
            "from sys.index_columns ic\n" +
            "inner join sys.columns c\n" +
            "    on c.object_id = ic.object_id\n" +
            "and c.column_id = ic.column_id\n" +
            "where ic.object_id = ?\n" +
            "  and index_id = ?\n" +
            "order by key_ordinal";

        DataTable dataTable = queryExecutor.execute(query, objectId, indexId);

        for (DataRow row : dataTable.getRows()) {
            String columnName = (String) row.get("column_name");
            boolean isDescending = (boolean) row.get("is_descending_key");
            boolean isIncludedColumn = (boolean) row.get("is_included_column");

            if (!isIncludedColumn)
            {
                SortType sort = isDescending ? SortType.DESC : SortType.ASC;
                IndexColumn indexColumn = new IndexColumn(columnName, sort);
                index.getKeyColumns().add(indexColumn);
            }
            else
            {
                ((NonClusteredIndex)index).getIncludedColumns().add(columnName);
            }
        }
    }
}
