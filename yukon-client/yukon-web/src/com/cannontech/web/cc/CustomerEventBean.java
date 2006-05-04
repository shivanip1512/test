package com.cannontech.web.cc;

import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.service.CustomerEventService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.cc.methods.BaseDetailBean;

public class CustomerEventBean {
    private CommercialCurtailmentBean commercialCurtailmentBean;
    private CustomerEventService customerEventService;
    private ListDataModel eventListModel = null;
    private EventDetailHelper eventDetailHelper;

    
    public DataModel getEventListModel() {
        if (eventListModel == null) {
            LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
            List<BaseEvent> currentEvents = customerEventService.getCurrentEvents(yukonUser);
            eventListModel = new ListDataModel(currentEvents);
        }
        return eventListModel;
    }
    
    public String showEventDetail() {
        BaseEvent event = (BaseEvent) getEventListModel().getRowData();
        BaseDetailBean detailBean = eventDetailHelper.getUserEventDetailBean(event);
        return detailBean.showDetail(event);
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
