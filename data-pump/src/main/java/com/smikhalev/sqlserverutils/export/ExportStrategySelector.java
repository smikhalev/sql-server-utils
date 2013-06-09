package com.smikhalev.sqlserverutils.export;

import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.export.ExportStrategy;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class ExportStrategySelector {

    private List<ExportStrategy> strategies;

    public ExportStrategySelector(List<ExportStrategy> strategies) {
        this.strategies = strategies;
    }

    public ExportStrategy select(Table table) {
        for(ExportStrategy strategy : strategies) {
            if (strategy.isApplicable(table))
                return strategy;
        }

        throw new ApplicationException("There is not applicable export strategy for table.");
    }
}
