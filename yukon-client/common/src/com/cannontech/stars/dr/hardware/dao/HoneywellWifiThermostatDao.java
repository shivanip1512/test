package com.cannontech.stars.dr.hardware.dao;

/**
 * Dao for retrieving data from HoneywellWifiThermostat table
 */
public interface HoneywellWifiThermostatDao {

    /**
     * Gets a macAddress based on the given deviceId.
     */
    String getMacAddressByDeviceId(int deviceId);

}
