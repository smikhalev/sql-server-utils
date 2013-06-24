package com.smikhalev.sqlserverutils;

import com.google.common.collect.Lists;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;
import com.smikhalev.sqlserverutils.schema.Database;

import java.util.ArrayList;
import java.util.List;

public class RestorableContext implements AutoCloseable {

    private List<RestorableAction> restorableActions;

    public RestorableContext() {
        this.restorableActions = new ArrayList<>();
    }

    public RestorableContext(Iterable<RestorableAction> restorableActions) {
        this.restorableActions = Lists.newArrayList(restorableActions);
    }

    public void prepare(Database database) {
        for(RestorableAction action : restorableActions) {
            action.act(database);
        }
    }

    public void prepare(List<RestorableAction> restorableActions, Database database) {
        this.restorableActions.addAll(restorableActions);

        for(RestorableAction action : restorableActions) {
            action.act(database);
        }
    }

    public void restore(){
        for(RestorableAction action : restorableActions) {
            action.restore();
        }
    }

    @Override
    public void close()  {
        restore();
    }
}
