package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.exportdata.BaseExportStrategy;
import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.NonClusteredIndex;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class IndexChunkStrategy extends BaseExportStrategy {
    private int chunkSize;
    private TableSizeProvider tableSizeProvider;
    private IndexSizeProvider indexSizeProvider;

    public IndexChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int chunkSize) {
        this.tableSizeProvider = tableSizeProvider;
        this.chunkSize = chunkSize;
        this.indexSizeProvider = indexSizeProvider;
    }

    protected abstract Index getIndex(Table table);

    public abstract boolean isApplicable(Table table);

    @Override
    public List<String> generateExportSelects(Table table) {
        List<String> selects = new ArrayList<>();

        long tableSize = tableSizeProvider.getSize(table);

        Index index = getIndex(table);

        for(long offset = 0; offset < tableSize; offset = offset + chunkSize) {
            String select = generateExportSelect(table, index, offset);
            selects.add(select);
        }

        return selects;
    }

    protected IndexSizeProvider getIndexSizeProvider() {
        return indexSizeProvider;
    }

    protected String generateExportSelect(Table table, Index index, long offset) {
        return generateSelectClause(table)
             + generateFromClause(table)
             + generateOrderByClause(index, offset);
    }

    protected String generateOrderByClause(Index index, long offset) {
        String order = Joiner.on(",").join(index.getKeyColumns());
        return String.format(" order by %s offset %s rows fetch next %s rows only", order, offset, chunkSize);
    }

    protected Index findSmallestNonClusteredIndex(Table table)  {
        Index minimumIndex = null;
        long minimumIndexSize = 0;
        for(NonClusteredIndex index : table.getNonClusteredIndexes()) {
            long indexSize = indexSizeProvider.getSize(index);
            if (minimumIndex == null || minimumIndexSize > indexSize)
            {
                minimumIndex = index;
                minimumIndexSize = indexSize;
            }
        }

        return minimumIndex;
    }
}