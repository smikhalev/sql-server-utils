package com.smikhalev.sqlserverutils.restorableaction;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.*;

public class CloneTableRestorableAction extends BaseRestorableAction {
    private final Table table;

    public CloneTableRestorableAction(StatementExecutor executor, Table table) {
        super(executor);
        this.table = table;
    }

    @Override
    public void act(Database database) {
        ClonedTable newTable = new ClonedTable(table);
        executeScript(newTable.generateCreateScript());
        addRestoreScript(newTable.generateDropScript());
    }
}
