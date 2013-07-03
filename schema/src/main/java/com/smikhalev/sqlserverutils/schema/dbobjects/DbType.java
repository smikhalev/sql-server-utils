package com.smikhalev.sqlserverutils.schema.dbobjects;

/**
 * http://technet.microsoft.com/en-us/library/ms378878.aspx
 */
public enum DbType {

    BIT("bit", DbSubType.INTEGER),
    TINYINT("tinyint", DbSubType.INTEGER),
    SMALLINT("smallint", DbSubType.INTEGER),
    INT("int", DbSubType.INTEGER),
    BIGINT("bigint", DbSubType.INTEGER),
    FLOAT("float", DbSubType.FLOAT),
    REAL("real", DbSubType.FLOAT),
    NVARCHAR("nvarchar", DbSubType.STRING),
    VARCHAR("varchar", DbSubType.STRING),
    DATE("date", DbSubType.DATETIME),
    TIME("time", DbSubType.DATETIME),
    DATETIME("datetime", DbSubType.DATETIME);

    private static enum DbSubType {
        INTEGER,
        STRING,
        FLOAT,
        DATETIME
    }

    private final String sqlType;
    private final DbSubType subType;

    DbType(String sqlType, DbSubType subType) {
        this.sqlType = sqlType;
        this.subType = subType;
    }

    public boolean isTimeType() {
        return subType == DbSubType.DATETIME;
    }

    public boolean isIntegerType() {
        return subType == DbSubType.INTEGER;
    }

    public boolean isFloatType() {
        return subType == DbSubType.FLOAT;
    }

    public boolean isStringType() {
        return subType == DbSubType.STRING;
    }

    public String getSqlType() {
        return sqlType;
    }
}
