package com.smikhalev.sqlserverutils.export.encoder;

import com.smikhalev.sqlserverutils.export.TypeValueEncoder;

public class DefaultTypeValueEncoder implements TypeValueEncoder {
    @Override
    public String encode(Object value) {
        return value.toString();
    }
}
