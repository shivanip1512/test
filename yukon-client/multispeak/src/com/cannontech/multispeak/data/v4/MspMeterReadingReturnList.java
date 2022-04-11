package com.cannontech.multispeak.data.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.MeterReading;

public class MspMeterReadingReturnList extends MspReturnList {

    private List<MeterReading> meterReading;

    public List<MeterReading> getMeterReading() {
        return meterReading;
    }

    public void setMeterReading(List<MeterReading> meterReading) {
        this.meterReading = meterReading;
    }

}
