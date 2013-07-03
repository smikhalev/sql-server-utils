package com.smikhalev.sqlserverutils.schema.dbobjects;


/*
 * This class represents the simplest foreign key which referenced just by one column.
 * That all we need right now for testing import functionality.
 */
public class ForeignKey extends DbObject {

    private final Table sourceTable;
    private final Column sourceColumn;
    private final Table targetTable;
    private final Column targetColumn;

    public ForeignKey(String name, Table sourceTable, Column sourceColumn, Table targetTable, Column targetColumn) {
        super(name, sourceTable.getSchema());
        this.sourceTable = sourceTable;
        this.sourceColumn = sourceColumn;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
    }

    @Override
    public String generateCreateScript() {
        String createFormat = "alter table %s with check add constraint %s foreign key(%s) references %s (%s)";
        return String.format(createFormat, sourceTable.getFullName(), getName(), sourceColumn.getName(), targetTable.getFullName(), targetColumn.getName());
    }

    @Override
    public String generateDropScript() {
        return String.format("alter table %s drop constraint [%s]", sourceTable.getFullName(), getName());
    }

    public Table getSourceTable() {
        return sourceTable;
    }

    public Column getSourceColumn() {
        return sourceColumn;
    }

    public Table getTargetTable() {
        return targetTable;
    }

    public Column getTargetColumn() {
        return targetColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ForeignKey fk = (ForeignKey) o;

        return sourceColumn.getName().equals(fk.getSourceColumn().getName())
            && sourceTable.getFullName().equals(fk.getSourceTable().getFullName())
            && targetColumn.getName().equals(fk.getTargetColumn().getName())
            && targetTable.getFullName().equals(fk.getTargetTable().getFullName());
    }
}










