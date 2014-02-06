package com.cannontech.dr.assetavailability.dao;

import java.util.Collection;
import java.util.Map;

import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;

/**
 * Dao for reading and writing to the DynamicLcrCommunications table. This table tracks the most recent communications 
 * times and most recently reported non-zero runtime times for 2-way LCRs.
 */
public interface DynamicLcrCommunicationsDao {
    /**
     * Retrieves last communication time and last non-zero run time for the specified devices. Does not deal with
     * specifics of runtime per relay/appliance. DeviceCommunicationTimes will be null for any device that has never
     * communicated.
     */
    public Map<Integer, DeviceCommunicationTimes> findTimes(Collection<Integer> deviceIds);
    
    /**
     * Retrieves last communication time, last non-zero run time, and last non-zero runtime for each relay for the
     * specified devices. Will return null for any device that has never communicated.
     */
    public Map<Integer, AllRelayCommunicationTimes> findAllRelayCommunicationTimes(Collection<Integer> deviceIds);
    
    /**
     * Inserts last communication time, last non-zero run time, and last non-zero runtime for each relay for the
     * specified device. If no entry exists for the device, one will be created. If an entry does exist, it will be
     * updated.
     */
    public void insertData(AssetAvailabilityPointDataTimes times);
}
