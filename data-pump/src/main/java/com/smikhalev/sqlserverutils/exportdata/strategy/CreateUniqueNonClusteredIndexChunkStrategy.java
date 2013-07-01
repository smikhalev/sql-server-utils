package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.exportdata.IndexSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.TableSizeProvider;
import com.smikhalev.sqlserverutils.exportdata.exporter.TableExportSelect;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;
import com.smikhalev.sqlserverutils.restorableaction.CloneTableRestorableAction;
import com.smikhalev.sqlserverutils.schema.dbobjects.ClonedTable;
import com.smikhalev.sqlserverutils.schema.dbobjects.Index;
import com.smikhalev.sqlserverutils.schema.dbobjects.SortType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.Arrays;
import java.util.List;

public class CreateUniqueNonClusteredIndexChunkStrategy extends UniqueNonClusteredCompositeChunkStrategy {

    private StatementExecutor executor;

    public CreateUniqueNonClusteredIndexChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, StatementExecutor executor, int chunkSize) {
        super(tableSizeProvider, indexSizeProvider, chunkSize);
        this.executor = executor;
    }

    @Override
    public TableExportSelect generateExportSelects(Table table) {
        return super.generateExportSelects(table);
    }

    @Override
    protected String generateExportSelect(Table table, Index index, int offset, int pageSize, SortType sortType) {
        ClonedTable clonedTable = new ClonedTable(table);
        return super.generateExportSelect(clonedTable, index, offset, pageSize, sortType);
    }

    @Override
    protected Index getIndex(Table table) {
        ClonedTable clonedTable = new ClonedTable(table);
        return clonedTable.getNonClusteredIndexes().get(0);
    }

    @Override
    public boolean isApplicable(Table table) {
        return true;
    }

    @Override
    protected List<RestorableAction> generateRestorableAction(Table table) {
        RestorableAction cloneTableRestorableAction = new CloneTableRestorableAction(executor, table);
        return Arrays.asList(cloneTableRestorableAction);
    }
}
