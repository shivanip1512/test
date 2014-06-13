package com.cannontech.stars.dr.controlHistory.service;

import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.user.YukonUserContext;

public interface ControlHistoryEventService {

    /**
     * This method takes a programId and list of event start/stop times and uses ControlHistoryEventDao to look
     * up the historical gear names in use for that program during the given event intervals.
     * If no gear name is found for an interval in the collection, then the first gear name found prior
     *  to the event is used.  If a name still is not found, the a default 'N/A' is returned for that interval.
     *  
     * @param programId The assigned program id for which the control event took place.
     * @param startDates A list of the beginning date & times of the control events.
     * @param endDates A list of the ending date & times of the control events.
     * @return A list of gear names whose order corresponds to the order of the startDates/endDates lists passed in.
     */
    List<String> getHistoricalGearNames(int programId, List<DateTime> startDates, List<DateTime> endDates, YukonUserContext userContext);
}
