package com.smikhalev.sqlserverutils.restorableaction;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.ForeignKey;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class DisableForeignKeysRestorableAction extends BaseRestorableAction {
    public DisableForeignKeysRestorableAction(StatementExecutor executor) {
        super(executor);
    }

    @Override
    public void act(Database database) {
        for(Table table : database.getTables()) {
            for(ForeignKey fk : table.getForeignKeys()) {
                executeScript(fk.generateDropScript());
                addRestoreScript(fk.generateCreateScript());
            }
        }
    }
}
