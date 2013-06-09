package com.smikhalev.sqlserverutils.export.sizeprovider;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.export.IndexSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;

import java.util.HashMap;

public class SystemViewIndexSizeProvider implements IndexSizeProvider {

    private StatementExecutor executor;
    private HashMap<String, Long> indexSizes;

    public SystemViewIndexSizeProvider(StatementExecutor executor) {
        this.executor = executor;
    }

    public long getSize(Index index) {
        if (indexSizes == null)
            initIndexSizes();

        String key = buildKey(index.getSchema(), index.getTable().getName(), index.getName());
        return indexSizes.get(key);
    }

    protected HashMap<String, Long> getIndexSizes() {
        return indexSizes;
    }

    protected void initIndexSizes() {
        indexSizes = new HashMap<>();

        DataTable table = loadIndexSizes();
        for(DataRow row : table.getRows()) {
            String schemaName = (String) row.get("schema_name");
            String tableName = (String) row.get("table_name");
            String indexName = (String) row.get("index_name");

            Long indexSize = (Long) row.get("index_size");

            String key = buildKey(schemaName, tableName, indexName);

            indexSizes.put(key, indexSize);
        }
    }

    protected String buildKey(String schemaName, String tableName, String indexName) {
        return DbObject.buildFullName(schemaName, tableName) + indexName;
    }

    private DataTable loadIndexSizes()  {
        String query =
            "select" +
            "    s.name as schema_name, " +
            "    t.name as table_name, " +
            "    i.name as index_name, " +
            "    sum(p.used_page_count) index_size " +
            "from sys.indexes i " +
            "inner join sys.tables t " +
            "    on i.object_id = t.object_id " +
            "inner join sys.schemas s " +
            "    on t.schema_id = s.schema_id " +
            "inner join  sys.dm_db_partition_stats as p " +
            "    on t.object_id = p.object_id " +
            "    and p.index_id = i.index_id " +
            "where i.name <> '' " +
            "group by s.name, t.name, i.name";

        return executor.executeAsDataTable(query);
    }
}
