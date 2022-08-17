package com.cannontech.amr.disconnect.model;

import org.joda.time.Instant;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.YukonMeter;

public class DisconnectMeterResult {
    
    private final YukonMeter meter;
    private DisconnectDeviceState state;
    private SpecificDeviceErrorDescription error;
    private Instant disconnectTime;
    private String processingException;
    private final DisconnectCommand command;
    
    public DisconnectMeterResult(YukonMeter meter,  DisconnectCommand command) {
        this.meter = meter;
        this.command = command;
    }

    public YukonMeter getMeter() {
        return meter;
    }

    public DisconnectDeviceState getState() {
        return state;
    }

    public void setState(DisconnectDeviceState state) {
        this.state = state;
    }

    public SpecificDeviceErrorDescription getError() {
        return error;
    }

    public void setError(SpecificDeviceErrorDescription error) {
        this.error = error;
    }

    public Instant getDisconnectTime() {
        return disconnectTime;
    }

    public void setDisconnectTime(Instant disconnectTime) {
        this.disconnectTime = disconnectTime;
    }

    public String getProcessingException() {
        return processingException;
    }

    public void setProcessingException(String processingException) {
        this.processingException = processingException;
    }

    public boolean isSuccess(){
        return state == DisconnectDeviceState.CONNECTED || state == DisconnectDeviceState.DISCONNECTED
               || state == DisconnectDeviceState.ARMED;
    }

    public DisconnectCommand getCommand() {
        return command;
    }
}
