package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.device.YukonDevice;

public abstract class ListBasedDeviceCollection implements DeviceCollection {

    @Override
    public Iterator<YukonDevice> iterator() {
        return getDeviceList().iterator();
    }

    public List<YukonDevice> getDevices(int start, int size) {

        List<YukonDevice> list = this.getDeviceList();

        int end = start + size;
        List<YukonDevice> subList = list.subList(start, Math.min(end, list.size()));

        return new ArrayList<YukonDevice>(subList);
    }
    
    @Override
    public long getDeviceCount() {
        return getDeviceList().size();
    }
    
}
