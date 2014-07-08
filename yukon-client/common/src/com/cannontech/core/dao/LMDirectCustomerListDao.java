package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDirectCustomerListDao {
   
    Set<LiteYukonPAObject> getLMProgramPaosForCustomer(Integer customerId);
    void setLMProgramPaosForCustomer(Integer customerId, Set<LiteYukonPAObject> lmProgramPaos);
    
}
