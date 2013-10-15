package com.cannontech.analysis.data.device;

import com.cannontech.amr.meter.model.YukonMeter;


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
    
    public YukonMeter getMeter() {
        return meterAndPointData.getMeter();
    }
}
