package com.smikhalev.sqlserverutils.importdata.restoreaction;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;

import java.util.Stack;

public abstract class BaseRestorableAction implements RestorableAction {

    private StatementExecutor executor;
    private Stack<String> restoreScripts = new Stack<>();

    public BaseRestorableAction(StatementExecutor executor) {
        this.executor = executor;
    }

    protected void executeScript(String script) {
        executor.executeScript(script);
    }

    protected void addRestoreScript(String script) {
        restoreScripts.push(script);
    }

    @Override
    public void restore() {
        while (!restoreScripts.isEmpty()) {
            executor.executeScript(restoreScripts.pop());
        }
    }
}
