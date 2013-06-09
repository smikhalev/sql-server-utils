package com.smikhalev.sqlserverutils.schema.exception;

import com.smikhalev.sqlserverutils.core.ApplicationException;

public class GenerateScriptException extends ApplicationException {
    public GenerateScriptException(String message) {
        super(message);
    }
}