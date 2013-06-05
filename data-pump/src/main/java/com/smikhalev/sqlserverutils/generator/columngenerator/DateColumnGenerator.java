package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class DateColumnGenerator implements ColumnGenerator {
    @Override
    public String generateValueScript() {
        return "dateadd(second, (cast(cast(newid() as binary(16)) as int) % (365*24*60*60)), getdate())";
    }
}
