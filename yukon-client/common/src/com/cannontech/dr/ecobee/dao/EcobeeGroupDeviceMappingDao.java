package com.cannontech.dr.ecobee.dao;

import java.util.List;

import com.google.common.collect.Multimap;

/**
 * Dao for getting ecobee groups and devices.
 */
public interface EcobeeGroupDeviceMappingDao {
    
    /**
     * Gets a multimap of ecobee load group ids to the serial number of the ecobee thermostats enrolled in each group.
     */
    public Multimap<String, String> getSerialNumbersByGroupId();
    
    public List<String> getAllEcobeeSerialNumbers();
}
