package com.cannontech.web.stars.dr.consumer.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableInventory;

public interface DisplayableInventoryDao {

    public List<DisplayableInventory> getDisplayableInventory(
            CustomerAccount customerAccount);
    
}
