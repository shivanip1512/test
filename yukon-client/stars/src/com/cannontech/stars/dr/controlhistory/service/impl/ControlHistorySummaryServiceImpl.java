package com.cannontech.stars.dr.controlhistory.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistorySummary;
import com.cannontech.stars.dr.controlhistory.service.ControlHistorySummaryService;
import com.cannontech.user.YukonUserContext;

public class ControlHistorySummaryServiceImpl implements ControlHistorySummaryService {
    
    private ControlHistoryEventDao controlHistoryEventDao;
    
    public ControlHistorySummary getControlSummary(int customerAccountId,
                                                   int inventoryId,
                                                   int groupId,
                                                   YukonUserContext userContext){
    
        ControlHistorySummary controlHistorySummary = new ControlHistorySummary();
        
        int dailySummary = 
            getControlHistoryTotalDuration(customerAccountId, inventoryId, groupId, ControlPeriod.PAST_DAY, userContext);
        controlHistorySummary.setDailySummary(dailySummary);
        
        int monthlySummary = 
            getControlHistoryTotalDuration(customerAccountId, inventoryId, groupId, ControlPeriod.PAST_MONTH, userContext);
        controlHistorySummary.setMonthlySummary(monthlySummary);
        
        int yearlySummary = 
            getControlHistoryTotalDuration(customerAccountId, inventoryId, groupId, ControlPeriod.PAST_YEAR, userContext);
        controlHistorySummary.setYearlySummary(yearlySummary);
        
        return controlHistorySummary;
     }
    
    private int getControlHistoryTotalDuration(final int customerAccountId,
                                               final int inventoryId,
                                               final int groupId, 
                                               final ControlPeriod period,
                                               final YukonUserContext userContext) {
        int results = 0;
        
        
        List<ControlHistoryEvent> controlHistoryEventList = 
            controlHistoryEventDao.toEventList(controlHistoryEventDao.getEventsByGroup(customerAccountId, 
                                                                                       groupId,
                                                                                       inventoryId,
                                                                                       period, 
                                                                                       userContext));

        for (ControlHistoryEvent controlHistoryEvent : controlHistoryEventList) {
            results += controlHistoryEvent.getDuration();
        }
        
        return results;
    }
    
    protected static class Holder {
        int groupId;
        int inventoryId;
    }
    
    @Autowired
    public void setControlHistoryEventDao(ControlHistoryEventDao controlHistoryEventDao) {
        this.controlHistoryEventDao = controlHistoryEventDao;
    }

}