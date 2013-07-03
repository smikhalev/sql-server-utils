package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.ExportStrategy;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.exporter.ExportSelect;
import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class TableSizeDependantStrategy implements ExportStrategy {

    private final ExportStrategy strategy;
    private final TableSizeProvider tableSizeProvider;
    private final long tableSizeLimit;

    public TableSizeDependantStrategy(ExportStrategy strategy, TableSizeProvider tableSizeProvider, long tableSizeLimit) {
        this.tableSizeLimit = tableSizeLimit;
        this.tableSizeProvider = tableSizeProvider;
        this.strategy = strategy;
    }

    @Override
    public ExportSelect generateExportSelects(Table table) {
        return strategy.generateExportSelects(table);
    }

    @Override
    public boolean isApplicable(Table table) {
        return tableSizeLimit > tableSizeProvider.getSize(table)
             && strategy.isApplicable(table);
    }

    @Override
    public List<RestorableAction> getRestorableActions(Table table) {
        return strategy.getRestorableActions(table);
    }

    public ExportStrategy getInnerStrategy() {
        return strategy;
    }

}
