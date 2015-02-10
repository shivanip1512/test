package com.cannontech.common.bulk.collection.device;

import java.util.Iterator;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;

public abstract class RangeBasedDeviceCollection implements DeviceCollection {

    @Override
    public Iterator<SimpleDevice> iterator() {
        return getDeviceList().iterator();
    }

    
    @Override
    public List<SimpleDevice> getDeviceList() {
        return getDevices(0, Integer.MAX_VALUE);
    }
    
    @Override
    public int getDeviceCount() {
        return getDeviceList().size();
    }
    
}
