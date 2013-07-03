package com.smikhalev.sqlserverutils.importdata.strategy;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.importdata.TableValueConstructor;
import com.smikhalev.sqlserverutils.restorableaction.DropNonClusteredIndexesRestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.Arrays;
import java.util.List;

public class BulkInsertDropNonClusteredIndexes extends BulkInsertImportStrategy {

    private StatementExecutor executor;

    public BulkInsertDropNonClusteredIndexes(TableValueConstructor constructor, StatementExecutor executor, int size) {
        super(constructor, size);
        this.executor = executor;
    }

    @Override
    public List<RestorableAction> getRestorableAction(Table table) {
        RestorableAction dropNonClusteredIndexes = new DropNonClusteredIndexesRestorableAction(executor, table);
        return Arrays.asList(dropNonClusteredIndexes);
    }

    @Override
    public boolean isApplicable(Table table) {
        return !table.getNonClusteredIndexes().isEmpty() || !table.isHeap();
    }
}
