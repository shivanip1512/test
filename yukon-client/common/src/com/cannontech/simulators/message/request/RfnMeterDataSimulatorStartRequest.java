package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class RfnMeterDataSimulatorStartRequest implements SimulatorRequest {

    private SimulatorSettings settings;
    private boolean isTest = false;

    public RfnMeterDataSimulatorStartRequest(SimulatorSettings settings) {
        this.settings = settings;
    }
    
    public RfnMeterDataSimulatorStartRequest(SimulatorSettings settings, boolean isTest) {
        this.settings = settings;
        this.isTest = true;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_METER;
    }

    public SimulatorSettings getSettings() {
        return settings;
    }
    
    public boolean isTest(){
        return isTest;
    }
}
