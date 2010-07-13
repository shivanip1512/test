package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Program;
import com.cannontech.database.data.lite.LiteEnergyCompany;

public interface CommonEventOperations<T extends BaseEvent> {

    List<T> getAllForEnergyCompany(LiteEnergyCompany energyCompany);
    List<T> getAllForCustomer(CICustomerStub customer);
    List<T> getAllForProgram(Program program);
}
