package com.cannontech.web.cc;

import java.util.Collections;
import java.util.List;

import javax.faces.model.ListDataModel;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.web.cc.methods.BaseDetailBean;

public class EventOverviewBean {
    private ListDataModel pendingEventListModel = null;
    private ListDataModel recentEventListModel = null;
    private ListDataModel currentEventListModel = null;
    private BaseEventDao baseEventDao;
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
            LiteEnergyCompany energyCompany = commercialCurtailmentBean.getEnergyCompany();
            List<BaseEvent> eventList = baseEventDao.getPendingEvents(energyCompany);
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
            LiteEnergyCompany energyCompany = commercialCurtailmentBean.getEnergyCompany();
            List<BaseEvent> eventList = baseEventDao.getCurrentEvents(energyCompany);
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
            LiteEnergyCompany energyCompany = commercialCurtailmentBean.getEnergyCompany();
            List<BaseEvent> eventList = baseEventDao.getRecentEvents(energyCompany);
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
    
    
    //setters for dependency injection

    public BaseEventDao getBaseEventDao() {
        return baseEventDao;
    }

    public void setBaseEventDao(BaseEventDao baseEventDao) {
        this.baseEventDao = baseEventDao;
    }

    public EventDetailHelper getEventDetailHelper() {
        return eventDetailHelper;
    }

    public void setEventDetailHelper(EventDetailHelper eventDetailHelper) {
        this.eventDetailHelper = eventDetailHelper;
    }


}
