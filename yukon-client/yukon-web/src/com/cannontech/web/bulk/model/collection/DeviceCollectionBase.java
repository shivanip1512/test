package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;

public abstract class DeviceCollectionBase implements DeviceCollection {

    protected String collectionKeyBase = "yukon.common.device.bulk.bulkAction.collection.";
    
    /**
     * Method to get a list of yukon devices that are in this device collection
     * @return List of yukon devices
     */
    protected abstract List<YukonDevice> getDevices();

    public Iterator<YukonDevice> getDeviceIterator() {
        return this.getDevices().iterator();
    }

    public List<YukonDevice> getDeviceList() {
        return this.getDevices();
    }

    public List<YukonDevice> getDevices(int start, int size) {

        List<YukonDevice> list = this.getDevices();

        int end = start + size;
        List<YukonDevice> subList = list.subList(start, Math.min(end,
                                                                 list.size()));

        return new ArrayList<YukonDevice>(subList);
    }

}
