package com.cannontech.web.cc;

import java.util.Collections;
import java.util.List;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.service.CustomerEventService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.cc.methods.BaseDetailBean;

public class CustomerEventBean {
    private CommercialCurtailmentBean commercialCurtailmentBean;
    private CustomerEventService customerEventService;
    private EventDetailHelper eventDetailHelper;
    private BaseEvent selectedEvent;

    public BaseEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(BaseEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
    
    public List<BaseEvent> getCurrentEventList() {
        LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
        List<BaseEvent> currentEvents = customerEventService.getCurrentEvents(yukonUser);
        Collections.reverse(currentEvents);
        return currentEvents;
    }
    
    public List<BaseEvent> getPendingEventList() {
        LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
        List<BaseEvent> pendingEvents = customerEventService.getPendingEvents(yukonUser);
        Collections.reverse(pendingEvents);
        return pendingEvents;
    }
    
    public List<BaseEvent> getRecentEventList() {
        LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
        List<BaseEvent> recentEvents = customerEventService.getRecentEvents(yukonUser);
        Collections.reverse(recentEvents);
        return recentEvents;
    }

    public String showEventDetail() {
        BaseDetailBean detailBean = eventDetailHelper.getUserEventDetailBean(selectedEvent);
        return detailBean.showDetail(selectedEvent);
    }
    
    
    // dependency injections getters/setters

    public CommercialCurtailmentBean getCommercialCurtailmentBean() {
        return commercialCurtailmentBean;
    }

    public void setCommercialCurtailmentBean(CommercialCurtailmentBean commercialCurtailmentBean) {
        this.commercialCurtailmentBean = commercialCurtailmentBean;
    }


    public CustomerEventService getCustomerEventService() {
        return customerEventService;
    }


    public void setCustomerEventService(CustomerEventService customerEventService) {
        this.customerEventService = customerEventService;
    }

    public EventDetailHelper getEventDetailHelper() {
        return eventDetailHelper;
    }

    public void setEventDetailHelper(EventDetailHelper eventDetailHelper) {
        this.eventDetailHelper = eventDetailHelper;
    }


}
