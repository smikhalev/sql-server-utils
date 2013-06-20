package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.ForeignKey;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.Stack;

public class ImportContext implements AutoCloseable {

    private Stack<String> restoreScripts = new Stack<>();
    private StatementExecutor executor;

    public ImportContext(StatementExecutor executor) {
        this.executor = executor;
    }

    public void disableForeignKeys(Database database) {
        for(Table table : database.getTables().values()) {
            for(ForeignKey fk : table.getForeignKeys()) {
                String dropFkScript = fk.generateDropScript();
                String createFkScript = fk.generateCreateScript();
                executor.executeScript(dropFkScript);
                restoreScripts.push(createFkScript);
            }
        }
    }

    public void restore(){
        for(String restoreScript : restoreScripts) {
            executor.executeScript(restoreScript);
        }
    }

    @Override
    public void close()  {
        restore();
    }
}
