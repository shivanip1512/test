package com.cannontech.web.cc.methods;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;
import com.cannontech.cc.service.NotificationService;
import com.cannontech.database.data.lite.LiteYukonUser;


public class DetailNotificationHelperBean {
    private NotificationService notificationService;
    private CurtailmentEvent event;
    private LiteYukonUser yukonUser;
    

    public CurtailmentEvent getEvent() {
        return event;
    }

    public void setEvent(CurtailmentEvent event) {
        this.event = event;
    }

    public List<CurtailmentEventNotif> getNotifList() {
        List<CurtailmentEventNotif> eventNotifs = getNotificationService().getEventNotifs(getEvent());
        return eventNotifs;
    }
    
    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    

}

