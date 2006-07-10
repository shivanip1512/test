package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.database.cache.functions.StandardDaoOperations;
import com.cannontech.enums.NotificationReason;

public interface EconomicEventNotifDao extends StandardDaoOperations<EconomicEventNotif> {
    public List<EconomicEventNotif> getForEvent(EconomicEvent event);
    public void deleteForEvent(EconomicEvent event);
    public List<EconomicEventNotif> getScheduledNotifs();
    public List<EconomicEventNotif> getForEventAndReason(EconomicEvent event, NotificationReason reason);
    public List<EconomicEventNotif> getForParticipant(EconomicEventParticipant participant);
}
