package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.MeterReading;


public class MspMeterReadReturnList extends MspReturnList {

    private List<MeterReading> meterReads;
    
    public List<MeterReading> getMeterReads() {
        return meterReads;
    }
    public void setMeterReads(List<MeterReading> meterReads) {
        this.meterReads = meterReads;
    }
}
