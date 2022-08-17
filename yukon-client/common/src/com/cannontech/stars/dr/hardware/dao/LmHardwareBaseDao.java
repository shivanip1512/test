package com.cannontech.stars.dr.hardware.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public interface LmHardwareBaseDao {
    
    /**
     * Returns the LMHardwareBase for a given id.
     * @param inventoryId
     * @return
     * @throws DataAccessException
     */
    public LMHardwareBase getById(int inventoryId) throws NotFoundException;
    
    /**
     * Returns the LMHardwareBase for a given serial number.
     * @param serialNumber
     * @return
     * @throws DataAccessException
     */
    public LMHardwareBase getBySerialNumber(String serialNumber) throws DataAccessException;
    
    /**
     * Deletes all configuration, mappings, events, installation info, and schedule info from the
     * database for a given inventory id.
     * @param inventoryId
     */
    public void clearLMHardwareInfo(Integer inventoryId);

    /**
     * Returns the serial number for the inventory with the given device id. 
     */
    public String getSerialNumberForDevice(int deviceId);

    /**
     * Returns the serial number for the inventory with the given inventory id. 
     */
    public String getSerialNumberForInventoryId(int inventoryId);

    /**
     * Returns the serial numbers for the supplied inventoryIds. 
     */
    public List<String> getSerialNumberForInventoryIds(Collection<Integer> inventoryId);
    
}