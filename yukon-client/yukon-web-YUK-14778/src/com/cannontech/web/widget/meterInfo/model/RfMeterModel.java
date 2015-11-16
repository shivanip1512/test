package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.amr.rfn.model.RfnMeter;

public class RfMeterModel extends MeterModel {
    
    private String serialNumber;
    private String manufacturer;
    private String model;
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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
    
    public static RfMeterModel of(RfnMeter meter) {
        
        RfMeterModel model = new RfMeterModel();
        model.setDeviceId(meter.getDeviceId());
        model.setDisabled(meter.isDisabled());
        model.setManufacturer(meter.getRfnIdentifier().getSensorManufacturer());
        model.setMeterNumber(meter.getMeterNumber());
        model.setModel(meter.getRfnIdentifier().getSensorModel());
        model.setName(meter.getName());
        model.setSerialNumber(meter.getRfnIdentifier().getSensorSerialNumber());
        
        return model;
    }
}