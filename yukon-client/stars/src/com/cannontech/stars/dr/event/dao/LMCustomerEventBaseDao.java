package com.cannontech.stars.dr.event.dao;

import com.cannontech.stars.dr.event.model.LMCustomerEventBase;

public interface LMCustomerEventBaseDao {

    public boolean addHardwareEvent(LMCustomerEventBase eventBase, int energyCompanyId, int inventoryId);
    
}
