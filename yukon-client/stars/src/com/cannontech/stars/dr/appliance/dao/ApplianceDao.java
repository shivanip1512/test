package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.stars.dr.appliance.model.Appliance;

public interface ApplianceDao {

    public void updateApplianceKW(int applianceId, float applianceKW);
    
    public void deleteAppliancesByAccountId(int accountId);
    
    public void deleteAppliancesByAccountIdAndInventoryId(int accountId, int inventoryId);
    
    public Appliance getById(int applianceId);
    
    public List<Appliance> getAssignedAppliancesByAccountId(int accountId);

    public List<Appliance> getByAccountId(int accountId);
    
    /**
     * This method returns the given appliance attached to the supplied accountId, programId, and 
     * inventoryId.  This method only returns one entry because a piece of inventory can only be enrolled
     * in a program once.
     */
    public Appliance getByAccountIdAndProgramIdAndInventoryId(int accountId, int programId, int inventoryId);

    public List<Integer> getApplianceIdsForAccountId(int accountId);
    
}
