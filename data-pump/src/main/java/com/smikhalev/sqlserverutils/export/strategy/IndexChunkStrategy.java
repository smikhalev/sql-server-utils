package com.smikhalev.sqlserverutils.export.strategy;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.export.BaseExportStrategy;
import com.smikhalev.sqlserverutils.export.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

public abstract class IndexChunkStrategy extends BaseExportStrategy {
    private int pageSize;
    private TableSizeProvider tableSizeProvider;

    public IndexChunkStrategy(TableSizeProvider tableSizeProvider, int pageSize) {
        this.tableSizeProvider = tableSizeProvider;
        this.pageSize = pageSize;
    }

    @Override
    public List<String> generateExportSelects(Table table) {
        List<String> selects = new ArrayList<>();

        long tableSize = tableSizeProvider.getSize(table);

        for(long offset = 0; offset < tableSize; offset = offset + pageSize) {
            String select = generateExportSelect(table, offset);
            selects.add(select);
        }

        return selects;
    }

    protected String generateExportSelect(Table table, long offset) {
        return generateSelectClause(table)
             + generateFromClause(table)
             + generateOrderByClause(table.getClusteredIndex(), offset);
    }

    protected String generateOrderByClause(Index index, long offset) {
        String order = Joiner.on(",").join(index.getKeyColumns());
        return String.format(" order by %s offset %s rows fetch next %s rows only", order, offset, pageSize);
    }

    public abstract boolean isApplicable(Table table);
}