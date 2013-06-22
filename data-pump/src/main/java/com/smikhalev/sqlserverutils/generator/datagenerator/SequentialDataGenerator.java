package com.smikhalev.sqlserverutils.generator.datagenerator;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.generator.ColumnGeneratorFactory;

public class SequentialDataGenerator extends BaseDataGenerator {
    public SequentialDataGenerator(ColumnGeneratorFactory columnGeneratorFactory, StatementExecutor executor, int chunkSize) {
        super(columnGeneratorFactory, executor, chunkSize);
    }

    @Override
    protected void generatePartData(String generateScript) {
        getExecutor().executeScript(generateScript);
    }
}
