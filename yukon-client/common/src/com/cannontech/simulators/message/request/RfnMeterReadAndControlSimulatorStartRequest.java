package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.simulators.SimulatorType;

// This is the old way of me handling messaging. This object is currently unused and will be deleted once I have confirmed that the other way works.
public class RfnMeterReadAndControlSimulatorStartRequest implements SimulatorRequest {
    
    private RfnMeterReadAndControlDisconnectSimulatorSettings settings;
    private boolean isTest = false;
    
    public RfnMeterReadAndControlSimulatorStartRequest() {
        settings = new RfnMeterReadAndControlDisconnectSimulatorSettings();
    }

    public RfnMeterReadAndControlSimulatorStartRequest(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        this.settings = settings;
    }
    
    public RfnMeterReadAndControlSimulatorStartRequest(RfnMeterReadAndControlDisconnectSimulatorSettings settings, boolean isTest) {
        this.settings = settings;
        this.isTest = true;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_METER_READ_AND_CONTROL;
    }

    public RfnMeterReadAndControlDisconnectSimulatorSettings getSettings() {
        return settings;
    }
    
    public boolean isTest(){
        return isTest;
    }
    
}
