package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;

public abstract class ListBasedDeviceCollection implements DeviceCollection {

    @Override
    public Iterator<SimpleDevice> iterator() {
        return getDeviceList().iterator();
    }

    public List<SimpleDevice> getDevices(int start, int size) {

        List<SimpleDevice> list = this.getDeviceList();

        int end = start + size;
        List<SimpleDevice> subList = list.subList(start, Math.min(end, list.size()));

        return new ArrayList<SimpleDevice>(subList);
    }
    
    @Override
    public long getDeviceCount() {
        return getDeviceList().size();
    }
    
}
