package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.schema.Database;

import java.util.List;
import java.util.Stack;

public class RestorableContext implements AutoCloseable {

    private final Stack<RestorableAction> restorableActions = new Stack<>();

    public RestorableContext() {
    }

    public RestorableContext(List<RestorableAction> restorableActions) {
        this.restorableActions.addAll(restorableActions);
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
        while(!restorableActions.isEmpty()){
            RestorableAction action = restorableActions.pop();
            action.restore();
        }
    }

    @Override
    public void close()  {
        restore();
    }
}
