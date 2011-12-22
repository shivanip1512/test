package com.cannontech.stars.dr.controlhistory.dao;

import java.util.List;

import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
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
 
}
