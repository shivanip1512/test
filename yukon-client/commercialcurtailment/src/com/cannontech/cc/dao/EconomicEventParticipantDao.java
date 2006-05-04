package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;

public interface EconomicEventParticipantDao extends StandardDaoOperations<EconomicEventParticipant> {
    public List<EconomicEventParticipant> getForEvent(EconomicEvent event);
    public void deleteForEvent(EconomicEvent event);
    public EconomicEventParticipant getForCustomerAndEvent(CICustomerStub customer, EconomicEvent event);
}
