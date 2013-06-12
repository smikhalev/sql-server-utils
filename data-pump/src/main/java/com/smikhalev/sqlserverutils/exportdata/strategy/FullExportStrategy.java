package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.BaseExportStrategy;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.Arrays;
import java.util.List;

public class FullExportStrategy extends BaseExportStrategy {

    @Override
    public List<String> generateExportSelects(Table table) {
        String select = generateSelectClause(table) + generateFromClause(table);
        return Arrays.asList(select);
    }

    @Override
    public boolean isApplicable(Table table) {
        return true;
    }
}
