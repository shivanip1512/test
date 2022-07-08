package com.cannontech.infrastructure.model;

import java.util.Set;

import com.cannontech.common.pao.PaoType;

/**
 * Represents the categories of devices that can have infrastructure warnings.
 */
public enum InfrastructureWarningDeviceCategory {
    GATEWAY(PaoType.getRfGatewayTypes()),
    RELAY(PaoType.getRfRelayTypes()),
    METER(PaoType.getMeterTypes()),
    CCU(PaoType.getCcuTypes()),
    REPEATER(PaoType.getRepeaterTypes())
    ;
    
    private Set<PaoType> paoTypes;
    
    private InfrastructureWarningDeviceCategory(Set<PaoType> paoTypes) {
        this.paoTypes = paoTypes;
    }
    
    public Set<PaoType> getPaoTypes() {
        return paoTypes;
    }
}
