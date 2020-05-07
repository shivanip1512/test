package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class ModifyRfnMeterReadAndControlSimulatorRequest implements SimulatorRequest{
    private static final long serialVersionUID = 1L;
    private boolean stopReadReply;
    private boolean stopDisconnectReply;
    
    private RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings;
    private RfnMeterReadAndControlReadSimulatorSettings readSettings;
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_METER_READ_CONTROL;
    }
    
    public boolean isStopReadReply() {
        return stopReadReply;
    }
    
    public void setStopReadReply(boolean stopReadReply) {
        this.stopReadReply = stopReadReply;
    }
    
    public boolean isStopDisconnectReply() {
        return stopDisconnectReply;
    }
    
    public void setStopDisconnectReply(boolean stopDisconnectReply) {
        this.stopDisconnectReply = stopDisconnectReply;
    }
    
    public RfnMeterReadAndControlDisconnectSimulatorSettings getDisconnectSettings() {
        return disconnectSettings;
    }
    
    public void setDisconnectSettings(RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings) {
        this.disconnectSettings = disconnectSettings;
    }
    
    public RfnMeterReadAndControlReadSimulatorSettings getReadSettings() {
        return readSettings;
    }
    
    public void setReadSettings(RfnMeterReadAndControlReadSimulatorSettings readSettings) {
        this.readSettings = readSettings;
    }
    
    public void setAllStop() {
        stopReadReply = true;
        stopDisconnectReply = true;
    }

    @Override
    public String toString() {
        return String.format("ModifyRfnMeterReadAndControlSimulatorRequest [stopReadReply=%s, stopDisconnectReply=%s, "
                + "disconnectSettings=%s, readSettings=%s]",
                stopReadReply, stopDisconnectReply, disconnectSettings, readSettings);
    }
}
