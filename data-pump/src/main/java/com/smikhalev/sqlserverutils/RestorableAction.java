package com.smikhalev.sqlserverutils;

import com.smikhalev.sqlserverutils.schema.Database;

public interface RestorableAction {
    public void act(Database database);
    public void restore();

}
