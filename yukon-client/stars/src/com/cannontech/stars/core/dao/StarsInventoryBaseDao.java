package com.cannontech.stars.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;

public interface StarsInventoryBaseDao {

    public LiteInventoryBase getById(int inventoryId);
    
    public List<LiteInventoryBase> getByIds(Set<Integer> inventoryIds);
    
}
