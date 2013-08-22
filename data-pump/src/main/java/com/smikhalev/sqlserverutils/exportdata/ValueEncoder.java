package com.smikhalev.sqlserverutils.exportdata;

import com.smikhalev.sqlserverutils.exportdata.ExportException;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;

public class ValueEncoder {

    public String encode(DbType type, Object value) {
        if (value == null)
            return "";

        if (type == DbType.BIT)
            return encodeBit(value);

        if (type.isIntegerType() )
            return encodeInt(value);

        if (type.isStringType())
            return encodeString(value);

        if (type.isTimeType())
            return encodeDate(value);

        if (type.isFloatType())
            return encodeFloat(value);

        throw new ExportException("Type " + type + " isn't supported.");
    }

    private String encodeString(Object value) {
        String originalValue = value.toString();
        return "\"'" + originalValue.replace("\"", "\"\"") + "'\"";
    }

    private String encodeInt(Object value) {
        return value.toString();
    }

    private String encodeFloat(Object value) {
        return "\"" + value + "\"";
    }

    private String encodeDate(Object value) {
        return "\"'" + value + "'\"";
    }

    private String encodeBit(Object value) {
        return value == false ? "0" : "1";
    }
}
