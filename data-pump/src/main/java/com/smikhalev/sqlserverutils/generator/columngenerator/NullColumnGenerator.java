package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class NullColumnGenerator implements ColumnGenerator {

    private ColumnGenerator columnGenerator;

    public NullColumnGenerator(ColumnGenerator columnGenerator) {
        this.columnGenerator = columnGenerator;
    }

    @Override
    public String generateValueScript() {
        return String.format("case when column_index %% 2 = 0 then %s end", columnGenerator.generateValueScript());
    }
}
