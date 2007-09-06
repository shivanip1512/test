package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.hardware.model.InventoryBase;

public interface InventoryBaseDao {

    public boolean add(InventoryBase inventoryBase);
    
    public boolean remove(InventoryBase inventoryBase);
    
    public boolean update(InventoryBase inventoryBase);
    
    public InventoryBase getById(int inventoryId) throws DataAccessException;
    
    public List<InventoryBase> getByAccountId(int accountId);
    
    public List<InventoryBase> getByInstallationCompanyId(int companyId);
    
    public List<InventoryBase> getByCategoryId(int categoryId);
    
    public List<InventoryBase> getAll();
    
}
