package com.cannontech.stars.dr.controlhistory.service.impl;

import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.MutableDuration;
import com.cannontech.common.util.OpenInterval;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistorySummary;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.service.ControlHistorySummaryService;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;

public class ControlHistorySummaryServiceImpl implements ControlHistorySummaryService {
    
    @Autowired private ControlHistoryEventDao controlHistoryEventDao;
    
    @Override
    public ControlHistorySummary getControlSummary(int customerAccountId, int inventoryId, int groupId, 
                                                   YukonUserContext userContext, boolean past){
    
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

    @Override
    public ControlHistorySummary getControlSummary(Collection<ControlHistoryEvent> controlHistoryEventList,
                                                   YukonUserContext userContext) {
        ControlHistorySummary controlHistorySummary = new ControlHistorySummary();
        
        // Past Day Summary
        Duration dailySummary = getControlHistoryTotalDuration(controlHistoryEventList, ControlPeriod.PAST_DAY, userContext);
        controlHistorySummary.setDailySummary(dailySummary);
        
        // Past Month Summary
        Duration monthlySummary = getControlHistoryTotalDuration(controlHistoryEventList, ControlPeriod.PAST_MONTH, userContext);
        controlHistorySummary.setMonthlySummary(monthlySummary);
        
        // Past Year Summary
        Duration yearlySummary = getControlHistoryTotalDuration(controlHistoryEventList, ControlPeriod.PAST_YEAR, userContext);
        controlHistorySummary.setYearlySummary(yearlySummary);
        
        return controlHistorySummary;
    }

    private Duration getControlHistoryTotalDuration(final int customerAccountId, int inventoryId, int groupId, 
                                                    ControlPeriod period, YukonUserContext userContext, boolean past) {

        StarsLMControlHistory eventsByGroup = controlHistoryEventDao.getEventsByGroup(customerAccountId, groupId, inventoryId, period, userContext, past);
        List<ControlHistoryEvent> controlHistoryEventList = controlHistoryEventDao.toEventList(null, eventsByGroup, userContext);

        return getControlHistoryTotalDuration(controlHistoryEventList, period, userContext);
    }

    private Duration getControlHistoryTotalDuration(Collection<ControlHistoryEvent> controlHistoryEventList,
                                                    ControlPeriod controlPeriod, YukonUserContext userContext) {
        MutableDuration results = new MutableDuration(0);
        
        StarsCtrlHistPeriod period = StarsCtrlHistPeriod.valueOf(controlPeriod.starsName());
        DateTime periodStartTime = LMControlHistoryUtil.getPeriodStartTime( period, userContext.getJodaTimeZone());
        OpenInterval startPeriodInterval = OpenInterval.createOpenEnd(periodStartTime);
        
        for (ControlHistoryEvent controlHistoryEvent : controlHistoryEventList) {
            OpenInterval controlHistoryInterval = OpenInterval.createClosed(controlHistoryEvent.getStartDate(), controlHistoryEvent.getEndDate());
            
            OpenInterval overlap = startPeriodInterval.overlap(controlHistoryInterval);
            if (overlap != null) {
                Duration displayedControlDuration = new Duration(overlap.getStart(), overlap.getEnd());
                results.plus(displayedControlDuration);
            }
        }
        
        return results.toDuration();
    }
    
    protected static class Holder {
        int groupId;
        int inventoryId;
    }
}