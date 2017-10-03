package com.cannontech.multispeak.data.v5;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.CDDevice;
import com.cannontech.multispeak.data.MspReturnList;

public class MspCDDeviceReturnList extends MspReturnList {
    private List<CDDevice> cdDevices;

    public List<CDDevice> getCDDevices() {
        if (cdDevices == null) {
            cdDevices = new ArrayList<>();
        }
        return cdDevices;
    }

    public void setCDDevices(List<CDDevice> meters) {
        this.cdDevices = meters;
    }
}
