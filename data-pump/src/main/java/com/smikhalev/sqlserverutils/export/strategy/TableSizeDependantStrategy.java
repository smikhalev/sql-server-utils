package com.smikhalev.sqlserverutils.export.strategy;

import com.smikhalev.sqlserverutils.export.ExportStrategy;
import com.smikhalev.sqlserverutils.export.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class TableSizeDependantStrategy implements ExportStrategy {

    private ExportStrategy strategy;
    private TableSizeProvider tableSizeProvider;
    private long tableSizeLimit;

    public TableSizeDependantStrategy(ExportStrategy strategy, TableSizeProvider tableSizeProvider, long tableSizeLimit) {
        this.tableSizeLimit = tableSizeLimit;
        this.tableSizeProvider = tableSizeProvider;
        this.strategy = strategy;
    }

    @Override
    public List<String> generateExportSelects(Table table) {
        return strategy.generateExportSelects(table);
    }

    @Override
    public boolean isApplicable(Table table) {
        return tableSizeLimit > tableSizeProvider.getSize(table)
             && strategy.isApplicable(table);
    }

    public ExportStrategy getInnerStrategy() {
        return strategy;
    }

}
