package com.cannontech.web.cc;

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
        return customerEventService.getCurrentEvents(yukonUser);
    }
    
    public List<BaseEvent> getPendingEventList() {
        LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
        return customerEventService.getPendingEvents(yukonUser);
    }
    
    public List<BaseEvent> getRecentEventList() {
        LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
        return customerEventService.getRecentEvents(yukonUser);
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
