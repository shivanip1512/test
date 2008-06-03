package com.cannontech.common.bulk.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.device.YukonDevice;

public abstract class IteratorBasedDeviceCollection implements DeviceCollection {

    public final  List<YukonDevice> getDeviceList() {
        Iterator<YukonDevice> iterator = iterator();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        List<YukonDevice> list = new ArrayList<YukonDevice>(100);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public final List<YukonDevice> getDevices(int start, int size) {

        List<YukonDevice> list = this.getDeviceList();

        int end = start + size;
        List<YukonDevice> subList = list.subList(start, Math.min(end,
                                                                 list.size()));

        return new ArrayList<YukonDevice>(subList);
    }
    
    @Override
    public long getDeviceCount() {
        return getDeviceList().size();
    }

}
