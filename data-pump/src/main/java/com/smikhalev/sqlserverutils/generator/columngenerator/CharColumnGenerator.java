package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class CharColumnGenerator implements ColumnGenerator {

    @Override
    public String generateValueScript() {
        return "cast(newid() as nvarchar(50))";
    }
}
