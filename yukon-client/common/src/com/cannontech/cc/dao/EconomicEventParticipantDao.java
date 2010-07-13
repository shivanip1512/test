package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface EconomicEventParticipantDao extends StandardDaoOperations<EconomicEventParticipant> {
    public EconomicEventParticipant getForCustomerAndEvent(CICustomerStub customer, EconomicEvent event);
    public List<EconomicEventParticipant> getForEvent(EconomicEvent event);
    public List<EconomicEventParticipant> getForCustomer(CICustomerStub customer);
    public List<EconomicEventParticipant> getForNotifs(List<EconomicEventNotif> notifList);
    public void deleteForEvent(EconomicEvent event);
}
