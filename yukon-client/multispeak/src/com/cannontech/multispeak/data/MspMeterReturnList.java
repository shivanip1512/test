package com.cannontech.multispeak.data;

import java.util.List;

import com.cannontech.msp.beans.v3.Meter;

public class MspMeterReturnList extends MspReturnList {

    private List<Meter> meters;

    public List<Meter> getMeters() {
        return meters;
    }

    public void setMeters(List<Meter> meters) {
        this.meters = meters;
    }
}
