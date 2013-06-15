package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.ValueEncoder;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;

public class SimpleValueEncoder implements ValueEncoder {
    private StringTypeValueEncoder stringValueEncoder = new StringTypeValueEncoder();
    private DefaultTypeValueEncoder defaultValueEncoder = new DefaultTypeValueEncoder();
    private BitTypeValueEncoder bitTypeValueEncoder = new BitTypeValueEncoder();

    public String encode(DbType type, Object value) {
        if (type.isStringType())
            return stringValueEncoder.encode(value);

        if (type == DbType.BIT)
            return bitTypeValueEncoder.encode(value);

        return defaultValueEncoder.encode(value);
    }
}
