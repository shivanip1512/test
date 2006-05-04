package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;
import com.cannontech.enums.NotificationReason;

public interface CurtailmentEventNotifDao extends StandardDaoOperations<CurtailmentEventNotif> {
    public List<CurtailmentEventNotif> getForEvent(CurtailmentEvent event);
    public void deleteForEvent(CurtailmentEvent event);
    public List<CurtailmentEventNotif> getScheduledNotifs();
    public List<CurtailmentEventNotif> getForEventAndReason(CurtailmentEvent event, NotificationReason reason);
}
