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
    private ListDataModel currentEventListModel = null;
    private EventDetailHelper eventDetailHelper;
    private ListDataModel pendingEventListModel;
    private ListDataModel recentEventListModel;

    private void updateListModels() {
        LiteYukonUser yukonUser = commercialCurtailmentBean.getYukonUser();
        
        List<BaseEvent> currentEvents = customerEventService.getCurrentEvents(yukonUser);
        currentEventListModel = new ListDataModel(currentEvents);
        
        List<BaseEvent> pendingEvents = customerEventService.getPendingEvents(yukonUser);
        pendingEventListModel = new ListDataModel(pendingEvents);

        List<BaseEvent> recentEvents = customerEventService.getRecentEvents(yukonUser);
        recentEventListModel = new ListDataModel(recentEvents);
    }
    public DataModel getCurrentEventListModel() {
        updateListModels();
        return currentEventListModel;
    }
    
    public String showCurrentEventDetail() {
        BaseEvent event = (BaseEvent) getCurrentEventListModel().getRowData();
        BaseDetailBean detailBean = eventDetailHelper.getUserEventDetailBean(event);
        return detailBean.showDetail(event);
    }
    
    public DataModel getPendingEventListModel() {
        updateListModels();
        return pendingEventListModel;
    }
    
    public String showPendingEventDetail() {
        BaseEvent event = (BaseEvent) getPendingEventListModel().getRowData();
        BaseDetailBean detailBean = eventDetailHelper.getUserEventDetailBean(event);
        return detailBean.showDetail(event);
    }
    
    public DataModel getRecentEventListModel() {
        updateListModels();
        return recentEventListModel;
    }
    
    public String showRecentEventDetail() {
        BaseEvent event = (BaseEvent) getRecentEventListModel().getRowData();
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
