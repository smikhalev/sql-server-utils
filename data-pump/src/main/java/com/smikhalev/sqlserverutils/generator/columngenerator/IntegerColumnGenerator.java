package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class IntegerColumnGenerator implements ColumnGenerator {

    private long typeSize;

    public IntegerColumnGenerator(long typeSize) {
        this.typeSize = typeSize;
    }

    @Override
    public String generateValueScript() {
        return String.format("cast(cast(newid() as binary(15)) as bigint) %% %s", typeSize);
    }
}
