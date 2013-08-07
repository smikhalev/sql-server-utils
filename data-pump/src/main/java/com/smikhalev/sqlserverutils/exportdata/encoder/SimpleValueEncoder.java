package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.ExportException;
import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;

public class SimpleValueEncoder implements ValueEncoder {
    private final StringTypeValueEncoder stringValueEncoder = new StringTypeValueEncoder();
    private final DateTypeValueEncoder dateTypeValueEncoder = new DateTypeValueEncoder();
    private final IntegerTypeValueEncoder intValueEncoder = new IntegerTypeValueEncoder();
    private final FloatTypeValueEncoder floatValueEncoder = new FloatTypeValueEncoder();
    private final BitTypeValueEncoder bitTypeValueEncoder = new BitTypeValueEncoder();

    public String encode(DbType type, Object value) {
        if (value == null)
            return "";

        if (type == DbType.BIT)
            return bitTypeValueEncoder.encode(value);

        if (type.isIntegerType() )
            return intValueEncoder.encode(value);

        if (type.isStringType())
            return stringValueEncoder.encode(value);

        if (type.isTimeType())
            return dateTypeValueEncoder.encode(value);

        if (type.isFloatType())
            return floatValueEncoder.encode(value);

        throw new ExportException("Type " + type + " isn't supported.");
    }
}
