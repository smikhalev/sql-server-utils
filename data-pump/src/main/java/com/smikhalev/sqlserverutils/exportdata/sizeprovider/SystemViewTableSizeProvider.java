package com.smikhalev.sqlserverutils.exportdata.sizeprovider;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.HashMap;

public class SystemViewTableSizeProvider implements TableSizeProvider {

    private StatementExecutor executor;
    private HashMap<String, Long> tableSizes;

    public SystemViewTableSizeProvider(StatementExecutor executor) {
        this.executor = executor;
    }

    public long getSize(Table table) {
        if (tableSizes == null)
            initTableSizes();

        return tableSizes.get(table.getFullName());
    }

    protected HashMap<String, Long> getTableSizes() {
        return tableSizes;
    }

    protected void initTableSizes() {
        tableSizes = new HashMap<>();

        DataTable table = loadTableSizes();
        for(DataRow row : table.getRows()) {
            String schemaName = (String) row.get("schema_name");
            String tableName = (String) row.get("table_name");
            Long tableSize = (Long) row.get("row_count");

            String key = DbObject.buildFullName(schemaName, tableName);

            tableSizes.put(key, tableSize);
        }
    }

    private DataTable loadTableSizes()  {
        String query =
            "select " +
            "    s.name as schema_name," +
            "    t.name as table_name," +
            "    sum(p.rows) as row_count\n" +
            "from sys.tables t\n" +
            "inner join sys.schemas s\n" +
            "    on t.schema_id = s.schema_id\n" +
            "inner join sys.partitions p\n" +
            "    on t.object_id = p.object_id\n" +
            "group by s.name, t.name";

        return executor.executeAsDataTable(query);
    }
}
