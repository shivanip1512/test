package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;

public class MspMeterReturnList extends MspReturnList {

    private List<ElectricMeter> meters;

    public List<ElectricMeter> getMeters() {
        return meters;
    }

    public void setMeters(List<ElectricMeter> meters) {
        this.meters = meters;
    }
}
