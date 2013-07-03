package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class RandomIntegerColumnGenerator implements ColumnGenerator {

    private final long typeSize;

    public RandomIntegerColumnGenerator(long typeSize) {
        this.typeSize = typeSize;
    }

    @Override
    public String generateValueScript() {
        return String.format("cast(cast(newid() as binary(15)) as bigint) %% %s", typeSize);
    }
}
