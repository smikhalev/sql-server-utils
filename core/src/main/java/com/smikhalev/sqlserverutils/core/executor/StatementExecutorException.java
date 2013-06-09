package com.smikhalev.sqlserverutils.core.executor;

import com.smikhalev.sqlserverutils.core.ApplicationException;

public class StatementExecutorException extends ApplicationException {
    public StatementExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
