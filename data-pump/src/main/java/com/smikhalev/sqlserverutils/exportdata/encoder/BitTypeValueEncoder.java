package com.smikhalev.sqlserverutils.exportdata.encoder;

import com.smikhalev.sqlserverutils.exportdata.TypeValueEncoder;

public class BitTypeValueEncoder implements TypeValueEncoder {
    @Override
    public String encode(Object value) {
        return value == false ? "0" : "1";
    }
}
