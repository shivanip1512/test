package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.amr.meter.model.PlcMeter;

public class PlcMeterModel extends MeterModel {
    
    private String address;
    private int routeId;
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public int getRouteId() {
        return routeId;
    }
    
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    
    public static PlcMeterModel of(PlcMeter meter) {
        
        PlcMeterModel model = new PlcMeterModel();
        model.setAddress(meter.getAddress());
        model.setDeviceId(meter.getDeviceId());
        model.setDisabled(model.isDisabled());
        model.setMeterNumber(meter.getMeterNumber());
        model.setName(meter.getName());
        model.setRouteId(meter.getRouteId());
        
        return model;
    }
}