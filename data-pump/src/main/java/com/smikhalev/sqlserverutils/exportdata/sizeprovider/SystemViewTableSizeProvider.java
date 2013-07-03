package com.smikhalev.sqlserverutils.exportdata.sizeprovider;

import com.smikhalev.sqlserverutils.core.executor.DataRow;
import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.HashMap;

/*
 * SQL Server doesn't guarantee that row count will be accurate.
 * All documentation says that row_count is approximate count.
 * http://msdn.microsoft.com/en-us/library/ms175012.aspx
 * Is absolutely understandable that during any load or activity it will be approximate.
 * The real question is will be this row_count accurate without any load?
 */
public class SystemViewTableSizeProvider implements TableSizeProvider {

    private final StatementExecutor executor;
    private volatile HashMap<String, Integer> tableSizes;
    private volatile Long databaseSize;

    public SystemViewTableSizeProvider(StatementExecutor executor) {
        this.executor = executor;
    }

    @Override
    public int getSize(Table table) {
        return getTableSizes().get(table.getFullName());
    }

    @Override
    public long getDatabaseSize() {
        if (databaseSize == null) {
            synchronized (this){
                if (databaseSize == null) {
                    initDatabaseSize();
                }
            }
        }
        return databaseSize;
    }

    private void initDatabaseSize() {
        if (databaseSize != null)
            return;

        databaseSize = (long) 0;

        for(Integer size : getTableSizes().values()){
            databaseSize += size;
        }
    }

    protected HashMap<String, Integer> getTableSizes() {
        if (tableSizes == null){
            synchronized (this){
                if (tableSizes == null)
                    initTableSizes();
            }
        }

        return tableSizes;
    }

    protected void initTableSizes() {
        tableSizes = new HashMap<>();

        DataTable table = loadTableSizes();
        for(DataRow row : table.getRows()) {
            String schemaName = (String) row.get("schema_name");
            String tableName = (String) row.get("table_name");
            Integer tableSize = (Integer) row.get("row_count");

            String key = DbObject.buildFullName(schemaName, tableName);

            tableSizes.put(key, tableSize);
        }
    }

    private DataTable loadTableSizes()  {
        String query =
            "select " +
            "    s.name as schema_name," +
            "    t.name as table_name," +
            "    cast(sum(p.rows) as int) as row_count\n" +
            "from sys.tables t\n" +
            "inner join sys.schemas s\n" +
            "    on t.schema_id = s.schema_id\n" +
            "inner join sys.partitions p\n" +
            "    on t.object_id = p.object_id\n" +
            "where index_id < 2\n" +
            "group by s.name, t.name";

        return executor.executeAsDataTable(query);
    }
}
