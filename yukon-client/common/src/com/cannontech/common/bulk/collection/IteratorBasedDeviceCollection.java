package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;

public abstract class IteratorBasedDeviceCollection implements DeviceCollection {

    public final  List<SimpleDevice> getDeviceList() {
        Iterator<SimpleDevice> iterator = iterator();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        List<SimpleDevice> list = new ArrayList<SimpleDevice>(100);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public final List<SimpleDevice> getDevices(int start, int size) {

        List<SimpleDevice> list = this.getDeviceList();

        int end = start + size;
        List<SimpleDevice> subList = list.subList(start, Math.min(end,
                                                                 list.size()));

        return new ArrayList<SimpleDevice>(subList);
    }
    
    @Override
    public long getDeviceCount() {
        return getDeviceList().size();
    }

}
