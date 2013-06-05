package com.smikhalev.sqlserverutils.schema.dbobjects;

import java.sql.Types;

/**
 * http://technet.microsoft.com/en-us/library/ms378878.aspx
 */
public enum DbType {

    BIT(Types.BIT, "bit"),
    TINYINT(Types.TINYINT, "tinyint"),
    SMALLINT(Types.SMALLINT, "smallint"),
    INT(Types.INTEGER, "int"),
    BIGINT(Types.BIGINT, "bigint"),
    FLOAT(Types.DOUBLE, "float"),
    REAL(Types.REAL, "real"),
    NVARCHAR(Types.NVARCHAR, "nvarchar"),
    VARCHAR(Types.VARCHAR, "varchar"),
    DATE(Types.DATE, "date"),
    TIME(Types.TIME, "time"),
    DATETIME(Types.TIMESTAMP, "datetime");

    private int javaType;
    private String sqlType;

    DbType(int javaType, String sqlType) {
        this.javaType = javaType;
        this.sqlType = sqlType;
    }

    public int getJavaType() {
        return javaType;
    }

    public String getSqlType() {
        return sqlType;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
