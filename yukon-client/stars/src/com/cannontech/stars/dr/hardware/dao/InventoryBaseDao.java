package com.cannontech.stars.dr.hardware.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.hardware.model.InventoryBase;

public interface InventoryBaseDao {

    public boolean add(InventoryBase inventoryBase);
    
    public boolean remove(InventoryBase inventoryBase);
    
    public boolean update(InventoryBase inventoryBase);
    
    public InventoryBase getById(int inventoryId);
    
    public Map<Integer, InventoryBase> getByIds(List<Integer> inventoryIdList);
    
    public List<InventoryBase> getByAccountId(int accountId);
    
    public List<Integer> getInventoryIdsByAccountId(int accountId);

    public List<InventoryBase> getDRInventoryByAccountId(int accountId);
    
    public List<InventoryBase> getByInstallationCompanyId(int companyId);
    
    public List<InventoryBase> getByCategoryId(int categoryId);
    
    public List<InventoryBase> getAll();
    
    public String getDisplayName(InventoryBase inventoryBase);

    public void uninstallInventory(Integer inventoryId);
    
    public List<InventoryBase> getByDeviceId(int deviceId);

    public InventoryBase findById(int inventoryId);
    
}
