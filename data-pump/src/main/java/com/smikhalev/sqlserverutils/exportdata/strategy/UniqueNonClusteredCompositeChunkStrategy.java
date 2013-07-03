package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.SortType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

/*
 * This strategy combine the first query from simple non clustered strategy
 * with unique non clustered strategy.
 */
public class UniqueNonClusteredCompositeChunkStrategy extends IndexChunkStrategy {
    private final NonClusteredIndexChunkStrategy nonClusteredIndexStrategy;
    private final UniqueNonClusteredIndexChunkStrategy uniqueNonClusteredIndexStrategy;

    public UniqueNonClusteredCompositeChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int chunkSize) {
        super(tableSizeProvider, indexSizeProvider, chunkSize);
        nonClusteredIndexStrategy = new NonClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, chunkSize);
        uniqueNonClusteredIndexStrategy = new UniqueNonClusteredIndexChunkStrategy(tableSizeProvider, indexSizeProvider, chunkSize);
    }

    @Override
    protected String generateExportSelect(Table table, Index index, int offset, int pageSize, SortType sortType) {
        if (offset == 0)
            return nonClusteredIndexStrategy.generateExportSelect(table, index, offset, pageSize, sortType);

        return uniqueNonClusteredIndexStrategy.generateExportSelect(table, index, offset, pageSize, sortType);
    }

    @Override
    protected Index getIndex(Table table) {
        return uniqueNonClusteredIndexStrategy.getIndex(table);
    }

    @Override
    public boolean isApplicable(Table table) {
        return uniqueNonClusteredIndexStrategy.isApplicable(table);
    }
}
