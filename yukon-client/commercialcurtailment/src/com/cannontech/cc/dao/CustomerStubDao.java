package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.database.data.lite.LiteCICustomer;

public interface CustomerStubDao extends StandardDaoOperations<CICustomerStub> {
    public List<CICustomerStub> getUnassignedCustomers(Group group);
    public List<CICustomerStub> getCustomersForEC(final Integer energyCompanyId);
    public CICustomerStub getForLite(LiteCICustomer liteCustomer);
}
