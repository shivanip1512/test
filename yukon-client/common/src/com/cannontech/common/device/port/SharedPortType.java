package com.cannontech.common.device.port;

import com.cannontech.database.db.port.CommPort;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum SharedPortType implements DatabaseRepresentationSource, DisplayableEnum {

    NONE(CtiUtilities.STRING_NONE), 
    ACS(CommPort.SHARE_ACS), 
    ILEX(CommPort.SHARE_ILEX);

    private final static ImmutableMap<String, SharedPortType> lookupBySharedPortTypeString;
    private final static Logger log = YukonLogManager.getLogger(SharedPortType.class);
    private String baseKey = "yukon.web.modules.operator.commChannel.sharedPortType.";

    static {
        try {
            ImmutableMap.Builder<String, SharedPortType> sharedPortTypeBuilder = ImmutableMap.builder();
            for (SharedPortType sharedPortType : values()) {
                sharedPortTypeBuilder.put(sharedPortType.sharedPortTypeString, sharedPortType);
            }
            lookupBySharedPortTypeString = sharedPortTypeBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps for Shared Port Type.", e);
            throw e;
        }
    }

    public static SharedPortType getSharedPortType(String sharedPortType) {
        SharedPortType portType = lookupBySharedPortTypeString.get(sharedPortType);
        checkArgument(portType != null, portType);
        return portType;
    }
    
    private String sharedPortTypeString;

    public String getSharedPortTypeString() {
        return sharedPortTypeString;
    }

    SharedPortType(String sharedPortTypeString) {
        this.sharedPortTypeString = sharedPortTypeString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return sharedPortTypeString;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
