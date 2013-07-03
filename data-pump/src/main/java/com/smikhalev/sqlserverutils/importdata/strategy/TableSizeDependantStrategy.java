package com.smikhalev.sqlserverutils.importdata.strategy;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.importdata.ImportStrategy;
import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class TableSizeDependantStrategy implements ImportStrategy {

    private final ImportStrategy strategy;
    private final TableSizeProvider tableSizeProvider;
    private final int tableSizeLimit;

    public TableSizeDependantStrategy(ImportStrategy strategy, TableSizeProvider tableSizeProvider, int tableSizeLimit){
        this.strategy = strategy;
        this.tableSizeProvider = tableSizeProvider;
        this.tableSizeLimit = tableSizeLimit;
    }

    @Override
    public String generateImportInsert(Packet packet) {
        return strategy.generateImportInsert(packet);
    }

    @Override
    public boolean isApplicable(Table table) {
        return tableSizeProvider.getSize(table) < tableSizeLimit && strategy.isApplicable(table);
    }

    @Override
    public List<RestorableAction> getRestorableAction(Table table) {
        return strategy.getRestorableAction(table);
    }

    @Override
    public int getSize() {
        return strategy.getSize();
    }
}
