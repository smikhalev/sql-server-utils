package com.smikhalev.sqlserverutils.cleardata;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class TruncateCleaner implements Cleaner {

    private Iterable<RestorableAction> restorableActions;
    private StatementExecutor executor;

    public TruncateCleaner(StatementExecutor executor, Iterable<RestorableAction> restorableActions) {
        this.restorableActions = restorableActions;
        this.executor = executor;
    }

    @Override
    public void clearData(Database database) {
        try(RestorableContext context = new RestorableContext(restorableActions)) {
            context.prepare(database);

            for(Table table : database.getTables()) {
                String truncateTableScript = String.format("truncate table %s", table.getFullName());
                executor.executeScript(truncateTableScript);
            }
        }
    }
}
