package com.cannontech.stars.dr.controlhistory.service.impl;

import java.util.List;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistorySummary;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.service.ControlHistorySummaryService;
import com.cannontech.user.YukonUserContext;

public class ControlHistorySummaryServiceImpl implements ControlHistorySummaryService {
    
    private ControlHistoryEventDao controlHistoryEventDao;
    
    public ControlHistorySummary getControlSummary(int customerAccountId,
                                                   int inventoryId,
                                                   int groupId,
                                                   YukonUserContext userContext, 
                                                   boolean past){
    
        ControlHistorySummary controlHistorySummary = new ControlHistorySummary();
        
        // Past Day Summary
        Duration dailySummary = getControlHistoryTotalDuration(customerAccountId, inventoryId, groupId, ControlPeriod.PAST_DAY, userContext, past);
        controlHistorySummary.setDailySummary(dailySummary);
        
        // Past Month Summary
        Duration monthlySummary = getControlHistoryTotalDuration(customerAccountId, inventoryId, groupId, ControlPeriod.PAST_MONTH, userContext, past);
        controlHistorySummary.setMonthlySummary(monthlySummary);
        
        // Past Year Summary
        Duration yearlySummary = getControlHistoryTotalDuration(customerAccountId, inventoryId, groupId, ControlPeriod.PAST_YEAR, userContext, past);
        controlHistorySummary.setYearlySummary(yearlySummary);
        
        return controlHistorySummary;
     }
    
    private Duration getControlHistoryTotalDuration(final int customerAccountId,
                                               int inventoryId,
                                               int groupId, 
                                               ControlPeriod period,
                                               YukonUserContext userContext,
                                               boolean past) {
        Duration results = new Duration(0);
        
        
        List<ControlHistoryEvent> controlHistoryEventList = 
            controlHistoryEventDao.toEventList(controlHistoryEventDao.getEventsByGroup(customerAccountId, 
                                                                                       groupId,
                                                                                       inventoryId,
                                                                                       period, 
                                                                                       userContext,
                                                                                       past));

        for (ControlHistoryEvent controlHistoryEvent : controlHistoryEventList) {
            results = controlHistoryEvent.getDuration().plus(results);
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