package com.cannontech.common.bulk.collection.device;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    
    @Override
    public Set<String> getErrorDevices() {
        return new HashSet<>();
    }

    @Override
    public int getDeviceErrorCount() {
        return 0;
    }

    @Override
    public String getUploadFileName() {
        return null;
    }

    @Override
    public String getHeader() {
        return null;
    }
    
}
