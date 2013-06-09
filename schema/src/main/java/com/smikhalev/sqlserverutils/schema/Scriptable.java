package com.smikhalev.sqlserverutils.schema;

public interface Scriptable {
    public String generateCreateScript();
    public String generateDropScript();
}
