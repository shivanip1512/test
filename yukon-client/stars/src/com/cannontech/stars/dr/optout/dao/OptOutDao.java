package com.cannontech.stars.dr.optout.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.OptOut;

public interface OptOutDao {

    public List<OptOut> getAll(CustomerAccount customerAccount);
    
}
