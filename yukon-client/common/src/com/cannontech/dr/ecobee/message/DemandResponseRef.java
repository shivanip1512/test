package com.cannontech.dr.ecobee.message;

/**
 * Identifies an ecobee demand response event. Used to cancel events.
 */
public final class DemandResponseRef {
    private final String demandResponseRef;
    
    public DemandResponseRef(String demandResponseRef) {
        this.demandResponseRef = demandResponseRef;
    }
    
    public String getDemandResponseRef() {
        return demandResponseRef;
    }
}
