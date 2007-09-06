package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public interface LMHardwareBaseDao {

    public boolean add(LMHardwareBase hardwareBase);
    
    public boolean remove(LMHardwareBase hardwareBase);
    
    public boolean update(LMHardwareBase hardwareBase);
    
    public LMHardwareBase getById(int inventoryId) throws DataAccessException;
    
    public LMHardwareBase getBySerialNumber(String serialNumber) throws DataAccessException;
    
    public List<LMHardwareBase> getByLMHardwareTypeId(int typeId);
    
    public List<LMHardwareBase> getByRouteId(int routeId);
    
    public List<LMHardwareBase> getByConfigurationId(int configurationId);
    
    public List<LMHardwareBase> getAll();
    
}
