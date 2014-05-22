package com.cannontech.dr.ecobee.dao;

import com.google.common.collect.Multimap;

/**
 * Dao for getting ecobee groups and devices.
 */
public interface EcobeeGroupDeviceMappingDao {
    
    /**
     * Gets a multimap of ecobee load group ids to the serial number of the ecobee thermostats enrolled in each group.
     */
    public Multimap<Integer, String> getGroupIdToSerialNumberMultimap();
}
