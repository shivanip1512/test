package com.cannontech.web.cc.methods;

import java.util.TimeZone;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.BaseNotificationStrategy;
import com.cannontech.cc.service.NotificationService;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.database.data.lite.LiteYukonUser;


public class DetailNotificationBean implements BaseDetailBean {
    private DetailNotificationHelperBean helper;
    private ProgramService programService;
    private NotificationService notificationService;
    private BaseNotificationStrategy strategy;
    private StrategyFactory strategyFactory;
    private CurtailmentEvent event;
    private LiteYukonUser yukonUser;
    
    public String showDetail(BaseEvent event) {
        setEvent((CurtailmentEvent) event);
        return "notifDetail";
    }

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
        
        return null;
    }

    public String deleteEvent() {
        getStrategy().deleteEvent(event,getYukonUser());
        return "programSelect";
    }

    public CurtailmentEvent getEvent() {
        return event;
    }

    public BaseNotificationStrategy getStrategy() {
        return strategy;
    }
    
    public void setEvent(CurtailmentEvent event) {
        this.event = event;
        strategy = (BaseNotificationStrategy) strategyFactory.getStrategy(event.getProgram());
        getHelper().setEvent(event);
        updateModels();
    }

    private void updateModels() {
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
