package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

/**
 * Request to create meters.
 */
public class DeviceArchiveSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private int serialFrom;
    private Integer serialTo;
    private String manufacturer;
    private String model;
   
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
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.DEVICE_ARCHIVE;
    }
}
