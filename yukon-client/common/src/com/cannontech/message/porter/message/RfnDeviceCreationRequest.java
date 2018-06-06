package com.cannontech.message.porter.message;

import com.cannontech.messaging.serialization.thrift.generated.RfnIdentifier;

public class RfnDeviceCreationRequest {
    private RfnIdentifier rfnIdentifier;
    
    public RfnDeviceCreationRequest(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public RfnDeviceCreationRequest() {}

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
}
