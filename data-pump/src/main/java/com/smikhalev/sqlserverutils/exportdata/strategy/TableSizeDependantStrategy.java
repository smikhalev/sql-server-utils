package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.ExportStrategy;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.exporter.TableExportSelect;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

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
    public TableExportSelect generateExportSelects(Table table) {
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
