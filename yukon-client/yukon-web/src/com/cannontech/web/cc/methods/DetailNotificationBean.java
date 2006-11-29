package com.cannontech.web.cc.methods;

import java.util.TimeZone;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.NotificationService;
import com.cannontech.cc.service.NotificationStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.cc.service.builder.CurtailmentChangeBuilder;
import com.cannontech.database.data.lite.LiteYukonUser;


public class DetailNotificationBean implements BaseDetailBean {
    private DetailNotificationHelperBean helper;
    private ProgramService programService;
    private NotificationService notificationService;
    private NotificationStrategy strategy;
    private StrategyFactory strategyFactory;
    private CurtailmentEvent event;
    private LiteYukonUser yukonUser;
    private CurtailmentChangeBuilder changeBuilder;
    
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

    public String prepareAdjustEvent() {
        changeBuilder = getStrategy().createChangeBuilder(getEvent());
        
        return "prepareAdjustEvent";
    }
    
    public String adjustEvent() {
        getStrategy().adjustEvent(changeBuilder, getYukonUser());
        
        return "notifDetail";
    }
    
    public String cancelAdjust() {
        return "notifDetail";
    }
    
    public String deleteEvent() {
        getStrategy().deleteEvent(event,getYukonUser());
        return "programSelect";
    }
    
    public String refresh() {
        updateModels();
        return null;
    }

    public CurtailmentEvent getEvent() {
        return event;
    }

    public NotificationStrategy getStrategy() {
        return strategy;
    }
    
    public void setEvent(CurtailmentEvent event) {
        this.event = event;
        strategy = (NotificationStrategy) strategyFactory.getStrategy(event.getProgram());
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

    public CurtailmentChangeBuilder getChangeBuilder() {
        return changeBuilder;
    }

}
