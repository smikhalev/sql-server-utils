package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class CastColumnGenerator implements ColumnGenerator {

    private final ColumnGenerator generator;
    private final String type;

    public CastColumnGenerator(ColumnGenerator generator, String type) {
        this.generator = generator;
        this.type = type;
    }

    @Override
    public String generateValueScript() {
        return String.format("cast(%s as %s)", generator.generateValueScript(), type);
    }
}
