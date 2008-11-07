package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public interface LMHardwareBaseDao {
    
    public boolean update(LMHardwareBase hardwareBase);
    
    /**
     * 
     * @param inventoryId
     * @return
     * @throws DataAccessException
     */
    public LMHardwareBase getById(int inventoryId) throws DataAccessException;
    
    /**
     * 
     * @param serialNumber
     * @return
     * @throws DataAccessException
     */
    public LMHardwareBase getBySerialNumber(String serialNumber) throws DataAccessException;
    
    /**
     * 
     * @param typeId
     * @return
     */
    public List<LMHardwareBase> getByLMHardwareTypeId(int typeId);
    
    /**
     * 
     * @param routeId
     * @return
     */
    public List<LMHardwareBase> getByRouteId(int routeId);
    
    /**
     * 
     * @param configurationId
     * @return
     */
    public List<LMHardwareBase> getByConfigurationId(int configurationId);
    
    /**
     * 
     * @return
     */
    public List<LMHardwareBase> getAll();

    /**
     * 
     * @param inventoryId
     */
    public void clearLMHardwareInfo(Integer inventoryId);
    
}
