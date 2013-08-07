package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.core.ApplicationException;

public class ExportException extends ApplicationException {
    public ExportException(String message) {
        super(message, null);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
