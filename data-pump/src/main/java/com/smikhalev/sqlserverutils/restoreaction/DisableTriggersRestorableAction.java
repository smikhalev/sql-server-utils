package com.smikhalev.sqlserverutils.restoreaction;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import com.smikhalev.sqlserverutils.schema.dbobjects.Trigger;

public class DisableTriggersRestorableAction extends BaseRestorableAction {
    public DisableTriggersRestorableAction(StatementExecutor executor) {
        super(executor);
    }

    @Override
    public void act(Database database) {
        for (Table table : database.getTables().values()) {
            for(Trigger trigger : table.getTriggers()) {
                executeScript(trigger.generateDisableScript());
                addRestoreScript(trigger.generateEnableScript());
            }
        }
    }
}
