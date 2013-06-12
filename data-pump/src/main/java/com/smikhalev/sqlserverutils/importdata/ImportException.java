package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.ApplicationException;

public class ImportException extends ApplicationException {
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
