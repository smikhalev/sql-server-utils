package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.importdata.RestorableAction;
import com.smikhalev.sqlserverutils.schema.Database;

public class RestorableContext implements AutoCloseable {

    private Iterable<RestorableAction> restoreActions;

    public RestorableContext(Iterable<RestorableAction> restoreActions) {
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
