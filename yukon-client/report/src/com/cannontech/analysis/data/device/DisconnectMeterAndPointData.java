package com.cannontech.analysis.data.device;

import com.cannontech.amr.meter.model.Meter;


public class DisconnectMeterAndPointData  {

    private MeterAndPointData meterAndPointData;
    private String disconnectAddress;
    
    public DisconnectMeterAndPointData(MeterAndPointData meterAndPointData, String discAddress) {
        this.meterAndPointData = meterAndPointData;
        this.disconnectAddress = discAddress;
    }
    
    public MeterAndPointData getMeterAndPointData() {
        return meterAndPointData;
    }
    
    public String getDisconnectAddress() {
        return disconnectAddress;
    }
    
    public Meter getMeter() {
        return meterAndPointData.getMeter();
    }
}
