package com.cannontech.web.cc.methods;

import java.util.TimeZone;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.BaseNotificationStrategy;
import com.cannontech.cc.service.NotificationService;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.util.JSFUtil;


public class DetailNotificationBean {
    private DetailNotificationHelperBean helper;
    private ProgramService programService;
    private NotificationService notificationService;
    private BaseNotificationStrategy strategy;
    private StrategyFactory strategyFactory;
    private CurtailmentEvent event;
    private Integer eventId;
    private LiteYukonUser yukonUser;
    

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    public TimeZone getTimeZone() {
        Program program = getEvent().getProgram();
        TimeZone tz = getProgramService().getTimeZone(program);
        return tz;
    }
    
    public Boolean getShowDeleteButton() {
        return getStrategy().canEventBeDeleted(getEvent(), getYukonUser());
    }
    
    public Boolean getShowCancelButton() {
        return getStrategy().canEventBeCancelled(getEvent(), getYukonUser());
    }
    
    public Boolean getShowAdjustButton() {
        return getStrategy().canEventBeAdjusted(getEvent(), getYukonUser());
    }
    
    public String cancelEvent() {
        getStrategy().cancelEvent(event,getYukonUser());
        
        return JSFUtil.redirect("/cc/notif/detail.jsf?eventId=" + getEventId());
    }

    public String deleteEvent() {
        getStrategy().deleteEvent(event,getYukonUser());
        return "programSelect";
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public CurtailmentEvent getEvent() {
        initialize();
        return event;
    }

    public BaseNotificationStrategy getStrategy() {
        initialize();
        return strategy;
    }
    
    private void initialize() {
        if (event == null) {
            event = getNotificationService().getEvent(eventId);
            strategy = (BaseNotificationStrategy) strategyFactory.getStrategy(event.getProgram());
            getHelper().setEvent(event);
        }
    }

    public void setEvent(CurtailmentEvent event) {
        this.event = event;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public DetailNotificationHelperBean getHelper() {
        return helper;
    }

    public void setHelper(DetailNotificationHelperBean helper) {
        this.helper = helper;
    }

    

}
