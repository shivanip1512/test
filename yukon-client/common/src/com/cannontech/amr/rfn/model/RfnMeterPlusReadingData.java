package com.cannontech.amr.rfn.model;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.common.rfn.model.RfnDevice;

public class RfnMeterPlusReadingData {

    private RfnDevice rfnDevice;
    private RfnMeterReadingData rfnMeterReadingData;
    
    public RfnMeterPlusReadingData(RfnDevice rfnDevice, RfnMeterReadingData rfnMeterReadingData) {
        this.rfnDevice = rfnDevice;
        this.rfnMeterReadingData = rfnMeterReadingData;
    }
    public RfnDevice getRfnDevice() {
        return rfnDevice;
    }
    public RfnMeterReadingData getRfnMeterReadingData() {
        return rfnMeterReadingData;
    }
    
}