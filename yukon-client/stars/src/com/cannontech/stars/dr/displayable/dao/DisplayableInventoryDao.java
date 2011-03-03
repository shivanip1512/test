package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.displayable.model.DisplayableInventory;

public interface DisplayableInventoryDao {

    public List<DisplayableInventory> getDisplayableInventory(int customerAccountId);

    /**
     * Returns a list of displayable inventory that support opt out.
     */
    public List<DisplayableInventory> getOptOutSupportingInventory(int accountId);
    
}
