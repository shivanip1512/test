package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;

public class SimulatedFirmwareVersionReplySettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private RfnUpdateServerAvailableVersionResult result = RfnUpdateServerAvailableVersionResult.SUCCESS;
    private String version = "1.2.3";
    
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
