package com.smikhalev.sqlserverutils.schema;

public interface Scriptable {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String COMMA_AND_NEW_LINE = ", " + NEW_LINE;

    public String generateCreateScript();
    public String generateDropScript();
}
