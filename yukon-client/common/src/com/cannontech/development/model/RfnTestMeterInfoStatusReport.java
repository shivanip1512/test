package com.cannontech.development.model;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.status.type.RfnMeterDisconnectMeterMode;
import com.cannontech.amr.rfn.message.status.type.RfnMeterDisconnectStateType;
import com.cannontech.common.rfn.model.RfnManufacturerModel;

public class RfnTestMeterInfoStatusReport {
    private int serialFrom = 1000;
    private Integer serialTo;
    private RfnManufacturerModel manufacturerModel = RfnManufacturerModel.RFN_420CL;
    private String manufacturerOverride;
    private String modelOverride;
    private String meterConfigurationId;
    private RfnMeterDisconnectMeterMode meterMode;
    private RfnMeterDisconnectStateType relayStatus;
    private Instant timestamp = new Instant();
    private boolean now = true;
    
    public int getSerialFrom() {
        return serialFrom;
    }
    public void setSerialFrom(int serialFrom) {
        this.serialFrom = serialFrom;
    }
    public Integer getSerialTo() {
        return serialTo;
    }
    public void setSerialTo(Integer serialTo) {
        this.serialTo = serialTo;
    }
    public RfnManufacturerModel getManufacturerModel() {
        return manufacturerModel;
    }
    public void setManufacturerModel(RfnManufacturerModel manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }
    public String getManufacturerOverride() {
        return manufacturerOverride;
    }
    public void setManufacturerOverride(String manufacturerOverride) {
        this.manufacturerOverride = manufacturerOverride;
    }
    public String getModelOverride() {
        return modelOverride;
    }
    public void setModelOverride(String model) {
        this.modelOverride = model;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public Long getTimestampAsMillis() {
        return timestamp.getMillis();
    }
    public boolean isNow() {
        return now;
    }
    public void setNow(boolean now) {
        this.now = now;
    }
    public String getMeterConfigurationId() {
        return meterConfigurationId;
    }
    public void setMeterConfigurationId(String meterConfigurationId) {
        this.meterConfigurationId = meterConfigurationId;
    }
    public RfnMeterDisconnectMeterMode getMeterMode() {
        return meterMode;
    }
    public void setMeterMode(RfnMeterDisconnectMeterMode meterMode) {
        this.meterMode = meterMode;
    }
    public RfnMeterDisconnectStateType getRelayStatus() {
        return relayStatus;
    }
    public void setRelayStatus(RfnMeterDisconnectStateType relayStatus) {
        this.relayStatus = relayStatus;
    }
}
