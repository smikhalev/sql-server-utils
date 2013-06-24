package com.smikhalev.sqlserverutils.exportdata.strategy;

import com.smikhalev.sqlserverutils.exportdata.BaseExportStrategy;
import com.smikhalev.sqlserverutils.exportdata.exporter.TableExportSelect;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.Arrays;

public class FullExportStrategy extends BaseExportStrategy {

    @Override
    public TableExportSelect generateExportSelects(Table table) {
        String select = generateSelectClause(table) + generateFromClause(table);
        return new TableExportSelect(table, Arrays.asList(select));
    }

    @Override
    public boolean isApplicable(Table table) {
        return true;
    }
}
