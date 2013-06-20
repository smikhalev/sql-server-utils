package com.smikhalev.sqlserverutils.generator.columngenerator;

import com.smikhalev.sqlserverutils.generator.ColumnGenerator;

public class SequenceIntegerColumnGenerator implements ColumnGenerator {
    @Override
    public String generateValueScript() {
        return "column_index";
    }
}
