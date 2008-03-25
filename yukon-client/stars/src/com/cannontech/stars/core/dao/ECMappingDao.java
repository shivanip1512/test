package com.cannontech.stars.core.dao;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public interface ECMappingDao {

    public LiteStarsEnergyCompany getCustomerAccountEC(CustomerAccount account);
    
}