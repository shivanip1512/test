package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.service.NotificationReason;

public interface CurtailmentEventNotifDao {
    public List<CurtailmentEventNotif> getForEvent(CurtailmentEvent event);
    public void deleteForEvent(CurtailmentEvent event);
    public List<CurtailmentEventNotif> getScheduledNotifs();
    public List<CurtailmentEventNotif> getForEventAndReason(CurtailmentEvent event, NotificationReason reason);
    public void deleteForParticipant(CurtailmentEventParticipant object);
    public void save(CurtailmentEventNotif object);
    public void delete(CurtailmentEventNotif object);
}
