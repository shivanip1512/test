package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public interface LMHardwareBaseDao {
    
    public boolean update(LMHardwareBase hardwareBase);
    
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
     * Returns a list of LMHardwareBase objects for a given type id.
     * @param typeId
     * @return
     */
    public List<LMHardwareBase> getByLMHardwareTypeId(int typeId);
    
    /**
     * Returns a list of LMHardwareBase object for a given route id.
     * @param routeId
     * @return
     */
    public List<LMHardwareBase> getByRouteId(int routeId);
    
    /**
     * Returns a list of LMHardwareBase objects for a given configuration id.
     * @param configurationId
     * @return
     */
    public List<LMHardwareBase> getByConfigurationId(int configurationId);
    
    /**
     * Returns a list of all LMHardwareBase objects.
     * @return
     */
    public List<LMHardwareBase> getAll();

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
    
}
