package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDirectCustomerListDao {
    
    public Set<Integer> getLMProgramIdsForCustomer(Integer customerId);
    public void setLMProgramIdsForCustomer(Integer customerId, Set<Integer> lmProgramIds);

    public Set<LiteYukonPAObject> getLMProgramPaosForCustomer(Integer customerId);
    public void setLMProgramPaosForCustomer(Integer customerId, Set<LiteYukonPAObject> lmProgramPaos);
    
}
