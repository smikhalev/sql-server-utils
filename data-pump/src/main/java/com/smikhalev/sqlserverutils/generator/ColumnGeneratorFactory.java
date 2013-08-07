package com.smikhalev.sqlserverutils.generator;

import com.smikhalev.sqlserverutils.generator.columngenerator.NullColumnGenerator;
import com.smikhalev.sqlserverutils.generator.columngenerator.SubstringColumnGenerator;
import com.smikhalev.sqlserverutils.schema.dbobjects.CharColumn;
import com.smikhalev.sqlserverutils.schema.dbobjects.Column;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;

import java.util.Map;

public class ColumnGeneratorFactory {

    private final Map<DbType, ColumnGenerator> columnGenerators;

    public ColumnGeneratorFactory(Map<DbType, ColumnGenerator> columnGenerators) {
        this.columnGenerators = columnGenerators;
    }

    public ColumnGenerator create(Column column)
    {
        ColumnGenerator generator = columnGenerators.get(column.getType());

        if (column instanceof CharColumn)
        {
            CharColumn charColumn = (CharColumn) column;
            generator = new SubstringColumnGenerator(generator, charColumn.getMaxCharLength());
        }

        if (column.isNull()) {
            return new NullColumnGenerator(generator);
        }

        return generator;
    }
}
