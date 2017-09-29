package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.MspMeter;
import com.cannontech.multispeak.data.MspReturnList;

public class MspMeterReturnList extends MspReturnList {

    private List<MspMeter> meters;

    public List<MspMeter> getMeters() {
        return meters;
    }

    public void setMeters(List<MspMeter> meters) {
        this.meters = meters;
    }
}
