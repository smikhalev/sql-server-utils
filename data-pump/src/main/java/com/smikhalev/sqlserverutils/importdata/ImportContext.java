package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.Database;

public class ImportContext implements AutoCloseable {

    private Iterable<RestorableAction> restoreActions;

    public ImportContext(Iterable<RestorableAction> restoreActions) {
        this.restoreActions = restoreActions;
    }

    public void prepare(Database database) {
        for(RestorableAction action : restoreActions) {
            action.act(database);
        }
    }

    public void restore(){
        for(RestorableAction action : restoreActions) {
            action.restore();
        }
    }

    @Override
    public void close()  {
        restore();
    }
}
