package com.cannontech.stars.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;

public interface StarsInventoryBaseDao {

    public LiteInventoryBase getById(int inventoryId);
    
    public List<LiteInventoryBase> getByIds(Set<Integer> inventoryIds);
    
    /**
     * @deprecated - Any call to this method should be refactored, loading all
     *               inventory from an EnergyCompany at one time is to heavy. 
     */
    @Deprecated
    public List<LiteInventoryBase> getAllByEnergyCompanyId(int energyCompanyId);
    
}
