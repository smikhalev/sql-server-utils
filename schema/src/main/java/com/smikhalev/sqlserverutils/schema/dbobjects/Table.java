package com.smikhalev.sqlserverutils.schema.dbobjects;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.schema.exception.GenerateScriptException;

import java.util.ArrayList;
import java.util.List;

public class Table extends DbObject {

    private ClusteredIndex clusteredIndex;
    private final Columns columns = new Columns();
    private final List<NonClusteredIndex> nonClusteredIndexes = new ArrayList<>();
    private final List<ForeignKey> foreignKeys = new ArrayList<>();
    private final List<Trigger> triggers = new ArrayList<>();

    public Table(String name, String schema) {
        super(name, schema);
    }

    public boolean isHeap() {
        return clusteredIndex == null;
    }

    public Columns getColumns() {
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

    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    @Override
    public String generateCreateScript() {
        String createTable = generateCreateTableScript();
        String createClusteredIndex = generateClusteredIndexScript();
        String createNonClusteredIndexes = generateNonClusteredScript();
        String createForeignKeys = generateForeignKeys();
        String createTriggers = generateCreateTriggers();

        return  createTable +
                createClusteredIndex +
                createNonClusteredIndexes +
                createForeignKeys +
                createTriggers;
    }

    private String generateForeignKeys() {
        List<String> foreignKeyScripts = new ArrayList<>();

        for(ForeignKey fk : foreignKeys) {
            foreignKeyScripts.add(fk.generateCreateScript());
        }
        return Joiner.on(Constants.NEW_LINE).join(foreignKeyScripts);
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

        return Joiner.on(Constants.NEW_LINE).join(nonClusteredIndexScripts);
    }

    private String generateCreateTableScript() {
        if (columns.isEmpty())
            throw new GenerateScriptException("Table should contain at least one column.");

        List<String> columnsWithTypes = new ArrayList<>();
        for(Column column : columns) {
            columnsWithTypes.add(column.generateCreateScript());
        }

        return String.format("create table %s (%s)", getFullName(), Joiner.on(",").join(columnsWithTypes));
    }

    private String generateCreateTriggers() {
        List<String> triggerScripts = new ArrayList<>();

        for(Trigger trigger : triggers) {
            triggerScripts.add(trigger.generateCreateScript());
        }

        return Joiner.on(Constants.NEW_LINE).join(triggerScripts);
    }

    @Override
    public String generateDropScript() {
        return generateDropTable();
    }

    private String generateDropTable() {
        return String.format(" drop table %s", getFullName());
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
            && nonClusteredIndexes.equals(table.getNonClusteredIndexes())
            && foreignKeys.equals(table.getForeignKeys())
            && triggers.equals(table.getTriggers());
    }
}
