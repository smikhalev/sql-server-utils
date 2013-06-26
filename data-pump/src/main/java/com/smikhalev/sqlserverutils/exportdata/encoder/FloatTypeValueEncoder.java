package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.TypeValueEncoder;

public class FloatTypeValueEncoder implements TypeValueEncoder {
    @Override
    public String encode(Object value) {
        return "\"" + value + "\"";
    }
}
