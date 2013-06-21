package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.Database;

public interface RestorableAction {
    public void act(Database database);
    public void restore();

}
