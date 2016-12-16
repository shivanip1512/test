package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.honeywellWifi.model.HoneywellWifiThermostat;

/**
 * Dao for retrieving data from HoneywellWifiThermostat table
 */
public interface HoneywellWifiThermostatDao {

    /**
     * Gets a HoneywellWifiThermostat(userId and macAddress) based on the given deviceId.
     */
    HoneywellWifiThermostat getHoneywellWifiThermostat(int deviceId);
    
    /**
     * @param The MAC ID, formatted with colons and capital letters (XX:XX:XX:XX:XX:XX).
     * @return The PaoIdentifier for the Honeywell thermostat with the specified MAC ID.
     * @throws NotFoundException if there is no device with the specified MAC ID.
     */
    PaoIdentifier getPaoIdentifierByMacId(String macId) throws NotFoundException;
    
    /**
     * @param  Group id in yukon
     * @return Group id to send to honeywell
     * @throws NotFoundException if there is no group with the specified group id.
     */
    int getHoneywellGroupId(int groupId) throws NotFoundException;
    
    /**
     * Gets the honey well groupIds with which the inventory was enrolled in the past.
     * 
     * @param inventoryId
     * @return List of past enrolled honeywell group ids list, this may have duplicate ids.
     */
    List<Integer> getPastEnrolledHoneywellGroupsByInventoryId(Integer inventoryId);
}
