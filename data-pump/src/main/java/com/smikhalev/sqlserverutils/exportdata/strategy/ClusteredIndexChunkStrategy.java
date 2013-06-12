package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class ClusteredIndexChunkStrategy extends IndexChunkStrategy {

    public ClusteredIndexChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int chunkSize) {
        super(tableSizeProvider, indexSizeProvider, chunkSize);
    }

    @Override
    protected Index getIndex(Table table) {
        return table.getClusteredIndex();
    }

    @Override
    public boolean isApplicable(Table table) {
        return !table.isHeap();
    }
}
