package com.cannontech.multispeak.data;

import java.util.List;

import com.cannontech.multispeak.deploy.service.Meter;

public class MspMeterReturnList extends MspReturnList {

    private List<Meter> meters;

    public List<Meter> getMeters() {
        return meters;
    }
    public void setMeters(List<Meter> meters) {
        this.meters = meters;
    }
}
