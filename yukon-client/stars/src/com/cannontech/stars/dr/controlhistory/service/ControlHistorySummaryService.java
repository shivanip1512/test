package com.cannontech.stars.dr.controlhistory.service;

import java.util.Collection;

import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistorySummary;
import com.cannontech.user.YukonUserContext;

public interface ControlHistorySummaryService {

    /**
     * This method returns a controlHistorySummary, which contains the amount of control
     * in the past day, past month, and past year, for a given piece of inventory that is
     * enrolled in a program.
     * 
     */
    public ControlHistorySummary getControlSummary(int customerAccountId, int inventoryId, int groupId, YukonUserContext userContext, boolean past);

    /**
     * This method returns a controlHistorySummary, which contains the amount of control
     * in the past day, past month, and past year, for a given list of ControlHistoryEvents.
     */
    public ControlHistorySummary getControlSummary(Collection<ControlHistoryEvent> controlHistoryEventList,
                                                   YukonUserContext userContext);

}