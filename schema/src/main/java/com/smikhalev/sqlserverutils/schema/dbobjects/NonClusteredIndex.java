package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.Constants;

import java.util.ArrayList;
import java.util.List;

public class NonClusteredIndex extends Index {
    private final List<String> includedColumns = new ArrayList<>();

    public NonClusteredIndex(String name, Table table, boolean isUnique) {
        super(name, table, isUnique);
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.NON_CLUSTERED;
    }

    public List<String> getIncludedColumns() {
        return includedColumns;
    }

    @Override
    public String generateCreateScript() {

        return super.generateCreateScript() + generateIncludeColumnPart();
    }

    private String generateIncludeColumnPart()
    {
        String result = "";

        if (includedColumns.size() > 0){
            String includedColumns = Joiner.on(Constants.COMMA_AND_NEW_LINE).join(getIncludedColumns());

            result = String.format("include (%s)", includedColumns);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        NonClusteredIndex that = (NonClusteredIndex) o;

        return super.equals(o)
            // The order of columns doesn't matter for included columns
            && includedColumns.containsAll(that.getIncludedColumns())
            && that.getIncludedColumns().containsAll(includedColumns);
    }
}
