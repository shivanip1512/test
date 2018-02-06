package com.cannontech.common.rfn.message;

import java.io.Serializable;
import java.util.Map;

/**
 * JMS Queue name: yukon.notif.obj.common.rfn.ArchiveStartupNotification
 */
public class RfnArchiveStartupNotification implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<RfnIdentifier, String> gatewayNames;

    public Map<RfnIdentifier, String> getGatewayNames() {
        return gatewayNames;
    }

    public void setGatewayNames(Map<RfnIdentifier, String> gatewayNames) {
        this.gatewayNames = gatewayNames;
    }
}
