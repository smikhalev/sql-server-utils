package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;

public class SimpleValueEncoder implements ValueEncoder {
    private StringTypeValueEncoder stringValueEncoder = new StringTypeValueEncoder();
    private DateTypeValueEncoder dateTypeValueEncoder = new DateTypeValueEncoder();
    private IntegerTypeValueEncoder intValueEncoder = new IntegerTypeValueEncoder();
    private FloatTypeValueEncoder floatValueEncoder = new FloatTypeValueEncoder();
    private BitTypeValueEncoder bitTypeValueEncoder = new BitTypeValueEncoder();

    public String encode(DbType type, Object value) {
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

        return intValueEncoder.encode(value);
    }
}
