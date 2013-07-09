package com.smikhalev.sqlserverutils.restorableaction;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.NonClusteredIndex;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.LinkedList;
import java.util.List;

public class DropIndexesRestorableAction extends BaseRestorableAction {

    private Table table;

    public DropIndexesRestorableAction(StatementExecutor executor, Table table) {
        super(executor);
        this.table = table;
    }

    @Override
    public void act(Database database) {
        if (!table.getNonClusteredIndexes().isEmpty()) {
            dropNonClusteredIndex();
        }

        if (!table.isHeap()) {
            dropClusteredIndex();
        }
    }

    private void dropClusteredIndex() {
        executeScript(table.getClusteredIndex().generateDropScript());
        addRestoreScript(table.getClusteredIndex().generateCreateScript());
    }

    private void dropNonClusteredIndex() {
        List<String> dropIndexes = new LinkedList<>();
        List<String> createIndexes = new LinkedList<>();

        for (NonClusteredIndex index : table.getNonClusteredIndexes()) {
            dropIndexes.add(index.generateDropScript());
            createIndexes.add(index.generateCreateScript());
        }

        String dropIndexesScript = Joiner.on(Constants.NEW_LINE).join(dropIndexes);
        String createIndexesScript = Joiner.on(Constants.NEW_LINE).join(createIndexes);

        executeScript(dropIndexesScript);
        addRestoreScript(createIndexesScript);
    }
}
