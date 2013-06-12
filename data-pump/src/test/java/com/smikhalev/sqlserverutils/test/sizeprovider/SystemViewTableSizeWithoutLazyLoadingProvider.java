package com.smikhalev.sqlserverutils.test.sizeprovider;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.sizeprovider.SystemViewTableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class SystemViewTableSizeWithoutLazyLoadingProvider extends SystemViewTableSizeProvider {
    public SystemViewTableSizeWithoutLazyLoadingProvider(StatementExecutor executor) {
        super(executor);
    }

    public long getSize(Table table) {
        initTableSizes();

        return getTableSizes().get(table.getFullName());
    }
}
