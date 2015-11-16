package com.cannontech.web.cc;

import java.util.Collections;
import java.util.List;

import javax.faces.model.ListDataModel;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.service.EventService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.web.cc.methods.BaseDetailBean;

public class EventOverviewBean {
    private ListDataModel pendingEventListModel = null;
    private ListDataModel recentEventListModel = null;
    private ListDataModel currentEventListModel = null;
    private EventService eventService;
    private EventDetailHelper eventDetailHelper;
    private CommercialCurtailmentBean commercialCurtailmentBean;
    private BaseEvent selectedEvent;
    
    
    public BaseEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(BaseEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public CommercialCurtailmentBean getCommercialCurtailmentBean() {
        return commercialCurtailmentBean;
    }

    public void setCommercialCurtailmentBean(CommercialCurtailmentBean commercialCurtailmentBean) {
        this.commercialCurtailmentBean = commercialCurtailmentBean;
    }

    public EventOverviewBean() {
        super();
    }

    public ListDataModel getPendingEventListModel() {
        if (pendingEventListModel == null) {
            EnergyCompany energyCompany = commercialCurtailmentBean.getEnergyCompany();
            List<BaseEvent> eventList = eventService.getPendingEventList(energyCompany);
            Collections.reverse(eventList);
            pendingEventListModel = new ListDataModel(eventList);
        }
        return pendingEventListModel;
    }

    public void setPendingEventListModel(ListDataModel pendingEventListModel) {
        this.pendingEventListModel = pendingEventListModel;
    }
    
    public ListDataModel getCurrentEventListModel() {
        if (currentEventListModel == null) {
            EnergyCompany energyCompany = commercialCurtailmentBean.getEnergyCompany();
            List<BaseEvent> eventList = eventService.getCurrentEventList(energyCompany);
            Collections.reverse(eventList);
            currentEventListModel = new ListDataModel(eventList);
        }
        return currentEventListModel;
    }

    public void setCurrentEventListModel(ListDataModel currentEventListModel) {
        this.currentEventListModel = currentEventListModel;
    }

    public ListDataModel getRecentEventListModel() {
        if (recentEventListModel == null) {
            EnergyCompany energyCompany = commercialCurtailmentBean.getEnergyCompany();
            List<BaseEvent> eventList = eventService.getRecentEventList(energyCompany);
            Collections.reverse(eventList);
            recentEventListModel = new ListDataModel(eventList);
        }
        return recentEventListModel;
    }

    public void setRecentEventListModel(ListDataModel recentEventListModel) {
        this.recentEventListModel = recentEventListModel;
    }

    public String showDetail() {
        return showDetail(getSelectedEvent());
    }
    
    private String showDetail(BaseEvent rowData) {
        BaseDetailBean eventDetailBean = eventDetailHelper.getEventDetailBean(rowData);
        return eventDetailBean.showDetail(rowData);
    }
    
    public String deleteEvent() {
        BaseEvent event = getSelectedEvent();
        eventService.forceDelete(event);
        return null;
    }
    
    //setters for dependency injection

    public EventDetailHelper getEventDetailHelper() {
        return eventDetailHelper;
    }

    public void setEventDetailHelper(EventDetailHelper eventDetailHelper) {
        this.eventDetailHelper = eventDetailHelper;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

}
