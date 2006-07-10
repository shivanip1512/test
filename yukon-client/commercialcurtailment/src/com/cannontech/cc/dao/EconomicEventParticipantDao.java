package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface EconomicEventParticipantDao extends StandardDaoOperations<EconomicEventParticipant> {
    public List<EconomicEventParticipant> getForEvent(EconomicEvent event);
    public void deleteForEvent(EconomicEvent event);
    public EconomicEventParticipant getForCustomerAndEvent(CICustomerStub customer, EconomicEvent event);
    public List<EconomicEventParticipant> getForCustomer(CICustomerStub customer);
}
