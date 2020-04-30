package com.cannontech.common.device.port;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ProtocolWrap implements DatabaseRepresentationSource, DisplayableEnum {

    None("None"),
    IDLC("IDLC");

    private String protocolWrapString;
    private String baseKey = "yukon.web.modules.operator.commChannel.protocolWrap.";

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

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
