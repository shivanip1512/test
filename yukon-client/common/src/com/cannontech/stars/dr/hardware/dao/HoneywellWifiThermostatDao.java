package com.cannontech.stars.dr.hardware.dao;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;

/**
 * Dao for retrieving data from HoneywellWifiThermostat table
 */
public interface HoneywellWifiThermostatDao {

    /**
     * Gets a macAddress based on the given deviceId.
     */
    String getMacAddressByDeviceId(int deviceId);
    
    /**
     * @param The MAC ID, formatted with colons and capital letters (XX:XX:XX:XX:XX:XX).
     * @return The PaoIdentifier for the Honeywell thermostat with the specified MAC ID.
     * @throws NotFoundException if there is no device with the specified MAC ID.
     */
    PaoIdentifier getPaoIdentifierByMacId(String macId) throws NotFoundException;
}
