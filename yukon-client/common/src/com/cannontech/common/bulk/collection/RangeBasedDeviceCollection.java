package com.cannontech.common.bulk.collection;

import java.util.Iterator;
import java.util.List;

import com.cannontech.common.device.YukonDevice;

public abstract class RangeBasedDeviceCollection implements DeviceCollection {

    @Override
    public Iterator<YukonDevice> iterator() {
        return getDeviceList().iterator();
    }

    
    @Override
    public List<YukonDevice> getDeviceList() {
        return getDevices(0, Integer.MAX_VALUE);
    }
    
    @Override
    public long getDeviceCount() {
        return getDeviceList().size();
    }
    
}
