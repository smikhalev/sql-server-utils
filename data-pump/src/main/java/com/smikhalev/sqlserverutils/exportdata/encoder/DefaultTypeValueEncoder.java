package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.TypeValueEncoder;

public class DefaultTypeValueEncoder implements TypeValueEncoder {
    @Override
    public String encode(Object value) {
        return value.toString();
    }
}