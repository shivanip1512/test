package com.cannontech.simulators.message.response;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;

public class RfnMeterReadAndControlSimulatorStatusResponse extends SimulatorResponseBase {

    private RfnDataSimulatorStatus status;
    private RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings;
    private RfnMeterReadAndControlReadSimulatorSettings readSettings;
    private boolean meterReadReplyActive;
    private boolean meterDisconnectReplyActive;

    public RfnMeterReadAndControlReadSimulatorSettings getReadSettings() {
        return readSettings;
    }

    public RfnMeterReadAndControlDisconnectSimulatorSettings getDisconnectSettings() {
        return disconnectSettings;
    }
    
    public void setDisconnectSettings(RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings) {
        this.disconnectSettings = disconnectSettings;
    }
    
    public void setReadSettings(RfnMeterReadAndControlReadSimulatorSettings readSettings) {
        this.readSettings = readSettings;
    }
    
    public RfnDataSimulatorStatus getStatus() {
        return status;
    }

    public boolean isMeterReadReplyActive() {
        return meterReadReplyActive;
    }

    public void setMeterReadReplyActive(boolean meterReadReplyActive) {
        this.meterReadReplyActive = meterReadReplyActive;
    }

    public boolean isMeterDisconnectReplyActive() {
        return meterDisconnectReplyActive;
    }

    public void setMeterDisconnectReplyActive(boolean meterDisconnectReplyActive) {
        this.meterDisconnectReplyActive = meterDisconnectReplyActive;
    }
    
    public int getNumberOfSimulatorsRunning() {
        int simulatorsRunning = 0;
        
        if (isMeterReadReplyActive()) {
            simulatorsRunning++;
        }
        if (isMeterDisconnectReplyActive()) {
            simulatorsRunning++;
        }
        
        return simulatorsRunning;
    }
}
