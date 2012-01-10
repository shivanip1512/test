package com.cannontech.multispeak.data;

import java.util.List;

import com.cannontech.multispeak.deploy.service.MeterRead;

public class MspMeterReadReturnList extends MspReturnList {

    private List<MeterRead> meterReads;
    
    public List<MeterRead> getMeterReads() {
        return meterReads;
    }
    public void setMeterReads(List<MeterRead> meterReads) {
        this.meterReads = meterReads;
    }
}
