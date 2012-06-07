package com.cannontech.stars.core.service;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.event.model.LMCustomerEventBase;
import com.cannontech.stars.dr.hardware.model.InventoryBase;

public interface StarsCacheService {

    public void updateInventoryCache(LiteStarsEnergyCompany energyCompany, InventoryBase inventoryBase, 
            CustomerAccount customerAccount, LMCustomerEventBase eventBase);
    
}
