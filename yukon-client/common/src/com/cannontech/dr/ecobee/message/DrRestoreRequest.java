package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.DemandResponseRef;

public final class DrRestoreRequest {
    private final String operation = "cancel";
    private final DemandResponseRef demandResponseRef;
    
    public DrRestoreRequest(String demandResponseRef) {
        this.demandResponseRef = new DemandResponseRef(demandResponseRef);
    }
    
    public String getOperation() {
        return operation;
    }
    
    public DemandResponseRef getDemandResponse() {
        return demandResponseRef;
    }
}
