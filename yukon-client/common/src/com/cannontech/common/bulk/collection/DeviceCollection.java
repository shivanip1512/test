package com.cannontech.common.bulk.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.device.YukonDevice;

/**
 * Wrapper class for a collection of YukonDevice
 */
public interface DeviceCollection {

    /**
     * Method to get the complete list of devices in this collection - for large
     * collections this method may take a while to return the list
     * @return List of yukon devices in this collection
     */
    public List<YukonDevice> getDeviceList();

    /**
     * Method to get an iterator for the devices in this collection
     * @return An iterator for the devices in this collection
     */
    public Iterator<YukonDevice> getDeviceIterator();

    /**
     * Method to get a list of devices from this collection
     * @param start - Index of first device to get
     * @param size - Number of devices to get including the start index
     * @return A list of devices from this collection
     */
    public List<YukonDevice> getDevices(int start, int size);

    /**
     * Method used to get the current map of parameters for this device
     * collection
     * @return Map of parameters
     */
    public Map<String, String> getCollectionParameters();

    /**
     * Method to get an ordered list of parameter values. Useful for I18N
     * messages
     * @return List of parameter values
     */
    public List<String> getParameterList();

    /**
     * Method to get the description I18N key of the collection of devices
     * @return String description key
     */
    public String getDescriptionKey();
}
