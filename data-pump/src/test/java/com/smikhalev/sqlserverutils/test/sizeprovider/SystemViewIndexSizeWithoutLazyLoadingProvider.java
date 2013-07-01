package com.smikhalev.sqlserverutils.test.sizeprovider;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.sizeprovider.SystemViewIndexSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;

public class SystemViewIndexSizeWithoutLazyLoadingProvider extends SystemViewIndexSizeProvider {
    public SystemViewIndexSizeWithoutLazyLoadingProvider(StatementExecutor executor) {
        super(executor);
    }

    @Override
    public long getSize(Index index) {
        initIndexSizes();

        String key = buildKey(index.getSchema(), index.getTable().getName(), index.getName());
        return getIndexSizes().get(key);
    }
}
