package com.smikhalev.sqlserverutils.export.strategy;

import com.smikhalev.sqlserverutils.export.IndexSizeProvider;
import com.smikhalev.sqlserverutils.export.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class UniqueClusteredIndexWithAnyNonClusteredIndexChunkStrategy extends IndexChunkSubQueryStrategy {
    public UniqueClusteredIndexWithAnyNonClusteredIndexChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int chunkSize) {
        super(tableSizeProvider, indexSizeProvider, chunkSize);
    }

    @Override
    public boolean isApplicable(Table table) {
        return !table.isHeap()
            && table.getClusteredIndex().isUnique()
            && !table.getNonClusteredIndexes().isEmpty();
    }

    @Override
    protected Index getIndex(Table table) {
        return table.getClusteredIndex();
    }

    @Override
    protected String generateInnerSelect(Table table, long offset, Index index) {
        return generateInnerSelectFields(index) +
               generateFromClause(table) +
               generateOrderByClause(findSmallestNonClusteredIndex(table), offset);
    }
}
