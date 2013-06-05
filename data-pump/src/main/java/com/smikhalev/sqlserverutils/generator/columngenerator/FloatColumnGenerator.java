package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class FloatColumnGenerator implements ColumnGenerator {
    @Override
    public String generateValueScript() {
        return "rand()";
    }
}
