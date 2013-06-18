package com.smikhalev.sqlserverutils.schema.exception;

import com.smikhalev.sqlserverutils.core.ApplicationException;

public class SchemaBuilderException extends ApplicationException {
    public SchemaBuilderException(String message) {
        super(message);
    }
}
