package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class SubstringColumnGenerator implements ColumnGenerator {

    private final ColumnGenerator columnGenerator;
    private final int size;

    public SubstringColumnGenerator(ColumnGenerator columnGenerator, int size) {
        this.columnGenerator = columnGenerator;
        this.size = size;
    }

    @Override
    public String generateValueScript() {
        return String.format("substring(%s, 1, %s)", columnGenerator.generateValueScript(), size);
    }
}
