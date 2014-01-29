package com.cannontech.dr.assetavailability.dao;

import java.util.Collection;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.assetavailability.DeviceCommunicationTimes;

/**
 * Dao for reading and writing to the LcrCommunications table. This table tracks the most recent communications times
 * and most recently reported non-zero runtime times for 2-way LCRs.
 */
public interface LcrCommunicationsDao {
    /**
     * Retrieves last communication time and last non-zero run time for the specified devices. Does not deal with
     * specifics of runtime per relay/appliance. DeviceCommunicationTimes will be null for any device that has never
     * communicated.
     */
    public Map<Integer, DeviceCommunicationTimes> getTimes(Collection<Integer> deviceIds);
    
    /**
     * Updates the last communicated time for the specified device to the specified timestamp, if this timestamp is
     * more recent than the existing value. If no entry currently exists for the device, a new one will be inserted. 
     * If one already exists, it will be updated.
     */
    public boolean updateComms(PaoIdentifier paoIdentifier, Instant timestamp);
    
    /**
     * Updates the last communicated time and/or the last runtime and/or the relay last runtime for the specified device
     * to the specified timestamp, if this timestamp is more recent than the existing value. If no entry currently 
     * exists for the device, a new one will be inserted. If one already exists, it will be updated.
     */
    public boolean updateRuntimeAndComms(PaoIdentifier paoIdentifier, int relay, Instant timestamp);
}
