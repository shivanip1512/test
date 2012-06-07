package com.cannontech.stars.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;

public interface StarsWorkOrderBaseDao {

    public LiteWorkOrderBase getById(int workOrderId);
    
    public List<LiteWorkOrderBase> getByIds(Collection<Integer> ids);
    
    public Map<Integer,LiteWorkOrderBase> getByIdsMap(Collection<Integer> ids);
    
    /**
     * @deprecated - This is a heavy database call. 
     */
    @Deprecated
    public List<LiteWorkOrderBase> getAll(int energyCompanyId);

    public void deleteByAccount(int accountId);

    public List<Integer> getByAccount(int accountId);
    
}
