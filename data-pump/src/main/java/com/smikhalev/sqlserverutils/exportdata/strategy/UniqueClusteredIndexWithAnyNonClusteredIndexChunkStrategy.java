package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
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
        return findSmallestNonClusteredIndex(table);
    }

    @Override
    protected String generateInnerSelect(Table table, Index index, long offset) {
        return generateInnerSelectFields(table.getClusteredIndex()) +
               generateFromClause(table) +
               generateOrderByClause(index, offset);
    }

    @Override
    protected String generateMainSelect(Table table, Index index, String innerSelect) {
        return generateSelectClause(table) +
               generateMainFromClause(table, table.getClusteredIndex(), innerSelect);
    }
}
