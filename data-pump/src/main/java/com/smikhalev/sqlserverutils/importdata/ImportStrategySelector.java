package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.ApplicationException;
import com.smikhalev.sqlserverutils.exportdata.ExportStrategy;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.List;

public class ImportStrategySelector {
    private List<ImportStrategy> strategies;

    public ImportStrategySelector(List<ImportStrategy> strategies) {
        this.strategies = strategies;
    }

    public ImportStrategy select(Table table) {
        for(ImportStrategy strategy : strategies) {
            if (strategy.isApplicable(table))
                return strategy;
        }

        throw new ApplicationException("There is not applicable import strategy for table.");
    }

}
