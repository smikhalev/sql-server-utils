package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.NonClusteredIndex;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class UniqueNonClusteredIndexChunkStrategy extends IndexChunkSubQueryStrategy {
    public UniqueNonClusteredIndexChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int chunkSize) {
        super(tableSizeProvider, indexSizeProvider, chunkSize);
    }

    @Override
    protected Index getIndex(Table table) {
        return findMinimumUniqueNonClusteredIndex(table);
    }

    @Override
    public boolean isApplicable(Table table) {
        return findMinimumUniqueNonClusteredIndex(table) != null;
    }

    private NonClusteredIndex findMinimumUniqueNonClusteredIndex(Table table) {
        NonClusteredIndex resultIndex = null;

        for (NonClusteredIndex index : table.getNonClusteredIndexes()) {
            if (index.isUnique()) {
                if (resultIndex == null) {
                    resultIndex = index;
                } else {
                    long resultIndexSize = getIndexSizeProvider().getSize(resultIndex);
                    long pretenderIndexSize = getIndexSizeProvider().getSize(index);

                    if (pretenderIndexSize < resultIndexSize) {
                        resultIndex = index;
                    }
                }
            }
        }

        return resultIndex;
    }
}
