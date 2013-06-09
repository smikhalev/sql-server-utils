package com.smikhalev.sqlserverutils.export.strategy;

import com.smikhalev.sqlserverutils.export.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class FullExportOfSmallTableStrategy extends FullExportStrategy {

    private int smallTableSize;
    private TableSizeProvider tableSizeProvider;

    public FullExportOfSmallTableStrategy(TableSizeProvider tableSizeProvider, int smallTableSize) {
        this.smallTableSize = smallTableSize;
        this.tableSizeProvider = tableSizeProvider;
    }

    @Override
    public boolean isApplicable(Table table) {
        return isSmallTable(table);
    }

    private boolean isSmallTable(Table table) {
        return tableSizeProvider.getSize(table) < smallTableSize;
    }
}
