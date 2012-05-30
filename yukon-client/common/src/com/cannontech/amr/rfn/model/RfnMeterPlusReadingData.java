package com.cannontech.amr.rfn.model;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;

public class RfnMeterPlusReadingData {

    private RfnMeter rfnMeter;
    private RfnMeterReadingData rfnMeterReadingData;
    
    public RfnMeterPlusReadingData(RfnMeter rfnMeter, RfnMeterReadingData rfnMeterReadingData) {
        this.rfnMeter = rfnMeter;
        this.rfnMeterReadingData = rfnMeterReadingData;
    }
    public RfnMeter getRfnMeter() {
        return rfnMeter;
    }
    public RfnMeterReadingData getRfnMeterReadingData() {
        return rfnMeterReadingData;
    }
    
}