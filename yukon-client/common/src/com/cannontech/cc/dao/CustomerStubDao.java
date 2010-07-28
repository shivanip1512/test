package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CICustomerPointData;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.core.dao.support.IdAccessible;
import com.cannontech.database.data.lite.LiteCICustomer;

public interface CustomerStubDao extends IdAccessible<CICustomerStub>{
    public List<CICustomerStub> getUnassignedCustomers(Group group);
    public List<CICustomerStub> getCustomersForEC(final Integer energyCompanyId);
    public CICustomerStub getForLite(LiteCICustomer liteCustomer);
    public CICustomerStub getForId(Integer id);
    public void save(CICustomerPointData customerPoint);
}
