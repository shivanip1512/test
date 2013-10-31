package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.stars.dr.appliance.model.Appliance;

public class MockApplianceDao implements ApplianceDao {

    @Override
    public void updateApplianceKW(int applianceId, float applianceKW) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void deleteAppliancesByAccountId(int accountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void deleteAppliancesByAccountIdAndInventoryId(int accountId, int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Appliance getById(int applianceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Appliance> getAssignedAppliancesByAccountId(int accountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Appliance> getByAccountId(int accountId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Appliance getByAccountIdAndProgramIdAndInventoryId(int accountId, int assignedProgramId,
                                                              int inventoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getApplianceIdsForAccountId(int accountId) {
        throw new MethodNotImplementedException();
    }

}
