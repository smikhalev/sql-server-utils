package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.TypeValueEncoder;

public class StringTypeValueEncoder implements TypeValueEncoder {
    @Override
    public String encode(Object value) {
        String originalValue = value.toString();
        return "\"" + originalValue.replace("\"", "\"\"") + "\"";
    }
}
