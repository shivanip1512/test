package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.database.data.lite.LiteEnergyCompany;

public interface CommonEventOperations {

    List<BaseEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany);
    List<BaseEvent> getAllForCustomer(CICustomerStub customer);

}
