package com.cannontech.multispeak.data.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.CDDevice;
import com.cannontech.multispeak.data.MspReturnList;

public class MspCDDeviceReturnList extends MspReturnList {
    private List<CDDevice> meters;

    public List<CDDevice> getCDMeters() {
        return meters;
    }

    public void setCDMeters(List<CDDevice> meters) {
        this.meters = meters;
    }
}
