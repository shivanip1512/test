package com.cannontech.common.rfn.simulation;

import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;

public class SimulatedFirmwareVersionReplySettings {
    RfnUpdateServerAvailableVersionResult result = RfnUpdateServerAvailableVersionResult.SUCCESS;
    String version = "1.2.3";
    
    public RfnUpdateServerAvailableVersionResult getResult() {
        return result;
    }
    
    public void setResult(RfnUpdateServerAvailableVersionResult result) {
        this.result = result;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
}
