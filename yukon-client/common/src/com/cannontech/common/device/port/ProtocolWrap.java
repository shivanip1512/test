package com.cannontech.common.device.port;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ProtocolWrap implements DatabaseRepresentationSource {

    None("None"), 
    IDLC("IDLC");

    private String ProtocolWrapString;

    private ProtocolWrap(String ProtocolWrap) {
        this.ProtocolWrapString = ProtocolWrap;
    }

    public String getProtocolWrapString() {
        return ProtocolWrapString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return ProtocolWrapString;
    }
}
