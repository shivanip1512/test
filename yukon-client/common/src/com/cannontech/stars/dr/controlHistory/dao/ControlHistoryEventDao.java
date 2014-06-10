package com.cannontech.stars.dr.controlHistory.dao;

import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.stars.dr.controlHistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;

public interface ControlHistoryEventDao {


    /**
     * This method gets all the control history for the given customerAccount, lmGroup, inventory, and control period.
     * It does however use a legacy stars control history object and will need an adapter of some sort to
     * migrate the data over to the new displayable objects..
     * 
     */
    public StarsLMControlHistory getEventsByGroup(int accountId, int lmGroupId, int inventoryId,
                                                  ControlPeriod period, YukonUserContext userContext, boolean past);

    /**
     * This method gets the latest control history event for the given inventory, program, and account.
     * It also figures in enrollment times to filter out any entries that may exist that are not apart of the
     * actual account.
     * 
     */
    public ControlHistoryEvent getLastControlHistoryEntry(int accountId, int programId,
                                                          int loadGroupId, int inventoryId,
                                                          YukonUserContext userContext, boolean past);
    
    /**
     * This method takes in an old stars control history object and generates the
     * a list of the ControlHistoryEvents, which we use in newer stars code.
     * 
     */
    public List<ControlHistoryEvent> toEventList(Integer programId, StarsLMControlHistory controlHistory, YukonUserContext userContext);
 
    /**
     * This method takes a programId and event start/stop times and attempts to look up the historical gear name
     * in use at that time.  If no gear name is found in the duration specified, then the first gear name found prior
     *  to the event is used.  If a name is still not found, the defaultName parameter is returned as the gear name.
     * @param programId The assigned program id for which the control event took place.
     * @param startDateTime The beginning of the control event.
     * @param endDateTime The end of the control event.
     * @return The gear name used during the control event, or the most recently used gear name, or default name.
     */
    String getHistoricalGearName(int programId, DateTime startDateTime, DateTime endDateTime, String defaultName);

}
