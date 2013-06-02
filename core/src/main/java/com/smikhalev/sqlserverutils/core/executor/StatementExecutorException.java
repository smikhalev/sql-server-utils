package com.smikhalev.sqlserverutils.core.executor;

public class StatementExecutorException extends RuntimeException {
    public StatementExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
