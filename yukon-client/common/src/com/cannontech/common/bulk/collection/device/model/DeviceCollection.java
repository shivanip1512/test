package com.cannontech.common.bulk.collection.device.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.device.model.SimpleDevice;

/**
 * Wrapper class for a collection of YukonDevice
 */
public interface DeviceCollection extends Iterable<SimpleDevice> {

    /**
     * Method to get the complete list of devices in this collection - for large
     * collections this method may take a while to return the list
     * @return List of yukon devices in this collection
     */
    public List<SimpleDevice> getDeviceList();
    
    /**
     * Method to get just the count of how many devices are in the collection. Should be faster
     * than creating actual devices and returning them if all you want is the count.
     * @return Count of how many devices are in the collection
     */
    public int getDeviceCount();

    /**
     * Method to get an iterator for the devices in this collection.
     * 
     * @return An iterator for the devices in this collection
     */
    @Override
    public Iterator<SimpleDevice> iterator();
    
    /**
     * Method to get a list of devices from this collection
     * @param start - Index of first device to get
     * @param size - Number of devices to get including the start index
     * @return A list of devices from this collection
     */
    public List<SimpleDevice> getDevices(int start, int size);

    /**
     * Method used to get the current map of parameters for this device
     * collection
     * @return Map of parameters
     */
    public Map<String, String> getCollectionParameters();

    /**
     * Method to get the description I18N key of the collection of devices
     * @return String description key
     */
    public MessageSourceResolvable getDescription();
    
    /**
     * Get the DeviceCollectionType of this collection.
     */
    public DeviceCollectionType getCollectionType();
    
    /**
     * Gets devices errors.
     * 
     * @return set of error devices
     */
    
    public default Set<String> getErrorDevices() {
        return Collections.emptySet();
    }

    
    /**
     * Gets error count.
     * @return Count of how many errors are in the collection
     */
    public default int getDeviceErrorCount(){
        return 0;
    }
    
    /**
     * Method to get upload file name.
     * @return Name of the file to be uploaded
     */
    public default String getUploadFileName(){
        return null;
    }
    /**
     * Method to get header.
     * @return Header 
     */
    public default String getHeader(){
        return null;
    }
}
