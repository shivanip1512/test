package com.cannontech.capcontrol.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CcEventType implements DatabaseRepresentationSource {
    BankStateUpdate(0),
    CommandSent(1),
    ManualCommand(2),
    IvvcCommStatus(16),
    IvvcTapOperation(19),
    IvvcRemoteControl(20),
    IvvcScanOperation(21);

    private int eventTypeId;
    
    private CcEventType(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return eventTypeId;
    }

}
