package com.smikhalev.sqlserverutils.export.encoder;

import com.smikhalev.sqlserverutils.export.TypeValueEncoder;

public class StringTypeValueEncoder implements TypeValueEncoder {
    @Override
    public String encode(Object value) {
        String originalValue = value.toString();
        return "\"" + originalValue.replace("\"", "\"\"") + "\"";
    }
}
