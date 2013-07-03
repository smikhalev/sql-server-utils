package com.smikhalev.sqlserverutils.schema.dbobjects;

public class CharColumn extends Column {

    private final int maxCharLength;

    public CharColumn(String name, DbType type, boolean isNull, int maxCharLength) {
        super(name, type, isNull);
        this.maxCharLength = maxCharLength;
    }

    public int getMaxCharLength() {
        return maxCharLength;
    }

    @Override
    public String generateCreateScript() {
        return String.format("[%s] [%s](%d)%s null",
                getName(), getType().getSqlType(), maxCharLength, isNull() ? "" : " not");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CharColumn column = (CharColumn) o;

        return super.equals(column)
                && maxCharLength == column.getMaxCharLength();
    }

}
