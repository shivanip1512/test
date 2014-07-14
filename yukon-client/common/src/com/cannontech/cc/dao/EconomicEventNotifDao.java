package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.service.NotificationReason;

public interface EconomicEventNotifDao {
    public void deleteForEvent(EconomicEvent event);
    public List<EconomicEventNotif> getScheduledNotifs();
    public List<EconomicEventNotif> getForEventAndReason(EconomicEvent event, NotificationReason reason);
    public List<EconomicEventNotif> getForParticipant(EconomicEventParticipant participant);
    public void save(EconomicEventNotif object);
    public void delete(EconomicEventNotif object);
}
