package com.cannontech.stars.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;

public interface StarsWorkOrderBaseDao {

    public LiteWorkOrderBase getById(int workOrderId);
    
    public List<LiteWorkOrderBase> getByIds(Collection<Integer> ids);
    
    public Map<Integer,LiteWorkOrderBase> getByIdsMap(Collection<Integer> ids);
    
    /**
     * @deprecated - This is a heavy database call. 
     */
    @Deprecated
    public List<LiteWorkOrderBase> getAll(int energyCompanyId);
    
}
