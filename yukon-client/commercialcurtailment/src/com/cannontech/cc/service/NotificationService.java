package com.cannontech.cc.service;

import java.util.List;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;

public class NotificationService {

    private CurtailmentEventDao curtailmentEventDao;
    private CurtailmentEventNotifDao curtailmentEventNotifDao;
    
    protected CurtailmentEventDao getCurtailmentEventDao() {
        return curtailmentEventDao;
    }
    
    public void setCurtailmentEventDao(CurtailmentEventDao curtailmentEventDao) {
        this.curtailmentEventDao = curtailmentEventDao;
    }
    
    protected CurtailmentEventNotifDao getCurtailmentEventNotifDao() {
        return curtailmentEventNotifDao;
    }
    
    public void setCurtailmentEventNotifDao(CurtailmentEventNotifDao curtailmentEventNotifDao) {
        this.curtailmentEventNotifDao = curtailmentEventNotifDao;
    }

    public CurtailmentEvent getEvent(Integer eventId) {
        return curtailmentEventDao.getForId(eventId);
    }

    public List<CurtailmentEventNotif> getEventNotifs(CurtailmentEvent event) {
        return curtailmentEventNotifDao.getForEvent(event);
    }


}
