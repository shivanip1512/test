package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.RfnNetworkManagerSimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class RfnNetworkManagerSimulatorStartRequest implements SimulatorRequest {
    
    private RfnNetworkManagerSimulatorSettings settings;
    private boolean isTest = false;
    
    public RfnNetworkManagerSimulatorStartRequest() {
        settings = new RfnNetworkManagerSimulatorSettings();
    }

    public RfnNetworkManagerSimulatorStartRequest(RfnNetworkManagerSimulatorSettings settings) {
        this.settings = settings;
    }
    
    public RfnNetworkManagerSimulatorStartRequest(RfnNetworkManagerSimulatorSettings settings, boolean isTest) {
        this.settings = settings;
        this.isTest = true;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_METER_NETWORK;
    }

    public RfnNetworkManagerSimulatorSettings getSettings() {
        return settings;
    }
    
    public boolean isTest(){
        return isTest;
    }
    
}
