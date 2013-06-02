package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.schema.exception.GenerateScriptException;

import java.util.ArrayList;
import java.util.List;

public class Table extends DbObject {

    private List<Column> columns = new ArrayList<>();
    private ClusteredIndex clusteredIndex;
    private List<NonClusteredIndex> nonClusteredIndexes = new ArrayList<>();

    public Table(String name, String schema) {
        super(name, schema);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public ClusteredIndex getClusteredIndex() {
        return clusteredIndex;
    }

    public void setClusteredIndex(ClusteredIndex clusteredIndex) {
        this.clusteredIndex = clusteredIndex;
    }

    public List<NonClusteredIndex> getNonClusteredIndexes() {
        return nonClusteredIndexes;
    }

    @Override
    public String generateCreateScript() {
        String createTable = generateCreateTableScript();
        String createClusteredIndex = generateClusteredIndexScript();
        String createNonClusteredIndexes = generateNonClusteredScript();

        return createTable + createClusteredIndex + createNonClusteredIndexes;
    }

    private String generateClusteredIndexScript() {
        return clusteredIndex != null
                    ? clusteredIndex.generateCreateScript()
                    : "";
    }

    private String generateNonClusteredScript() {
        if (nonClusteredIndexes.size() == 0)
            return "";

        List<String> nonClusteredIndexScripts = new ArrayList<>();
        for(Index index : nonClusteredIndexes) {
            nonClusteredIndexScripts.add(index.generateCreateScript());
        }

        return Joiner.on(NEW_LINE).join(nonClusteredIndexScripts);
    }

    private String generateCreateTableScript() {
        if (columns.isEmpty())
            throw new GenerateScriptException("Table should contain at least one column.");

        String columnsScript = Joiner.on(", ").join(columns);
        return String.format("create table %s (%s)", getFullName(), columnsScript);
    }

    @Override
    public String generateDropScript() {
        return String.format("drop table %s", getFullName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Table table = (Table) o;

        return super.equals(o)
            && columns.equals(table.getColumns())
            && ((clusteredIndex == null && table.getClusteredIndex() == null)
                || clusteredIndex.equals(table.getClusteredIndex()))
            && nonClusteredIndexes.equals(table.getNonClusteredIndexes());
    }
}
