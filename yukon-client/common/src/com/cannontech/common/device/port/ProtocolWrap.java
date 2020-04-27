package com.cannontech.common.device.port;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ProtocolWrap implements DatabaseRepresentationSource {

    None("None"), 
    IDLC("IDLC");

    private String protocolWrapString;

    private ProtocolWrap(String protocolWrap) {
        this.protocolWrapString = protocolWrap;
    }

    public String getProtocolWrapString() {
        return protocolWrapString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return protocolWrapString;
    }
}
