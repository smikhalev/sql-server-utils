package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.exportdata.BaseExportStrategy;
import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.exporter.TableExportSelect;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.*;

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
    public TableExportSelect generateExportSelects(Table table) {
        List<String> selects = new ArrayList<>();

        long tableSize = tableSizeProvider.getSize(table);

        Index index = getIndex(table);

        long leftSize = tableSize / 2;
        long rightSize = tableSize - leftSize;

        for(long offset = 0; offset < leftSize; offset = offset + chunkSize) {
            String select = generateExportSelect(table, index, offset, SortType.ASC);
            selects.add(select);
        }

        for(long offset = 0; offset < rightSize; offset = offset + chunkSize) {
            String select = generateExportSelect(table, index, offset, SortType.DESC);
            selects.add(select);
        }

        return new TableExportSelect(table, selects, generateRestorableAction(table));
    }

    protected List<RestorableAction> generateRestorableAction(Table table) {
        return new ArrayList<>();
    }

    protected IndexSizeProvider getIndexSizeProvider() {
        return indexSizeProvider;
    }

    protected String generateExportSelect(Table table, Index index, long offset, SortType sortType) {
        return generateSelectClause(table)
             + generateFromClause(table)
             + generateOrderByClause(index, offset, sortType);
    }

    protected String generateOrderByClause(Index index, long offset, SortType sortType) {
        String order;
        if (sortType == SortType.ASC) {
            order = Joiner.on(",").join(index.getKeyColumns());
        }
        else {
            List<String> orderByFields = new ArrayList<>();
            for(IndexColumn indexColumn : index.getKeyColumns()){
                SortType reversedSortType = SortType.reverseSort(indexColumn.getSort());
                String orderByField = indexColumn.getName() + " " + reversedSortType.name().toLowerCase();

                orderByFields.add(orderByField);
            }

            order = Joiner.on(",").join(orderByFields);
        }

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