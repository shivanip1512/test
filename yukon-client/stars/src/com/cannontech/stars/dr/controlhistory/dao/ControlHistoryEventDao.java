package com.cannontech.stars.dr.controlhistory.dao;

import java.util.List;

import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;

public interface ControlHistoryEventDao {

    public enum ControlPeriod {
        PAST_DAY("PastDay"),
        PAST_WEEK("PastWeek"),
        PAST_MONTH("PastMonth"),
        PAST_YEAR("PastYear"),
        ALL("All");
        
        private final String starsName;
        ControlPeriod(final String starsName) {
            this.starsName = starsName;
        }
        
        public String starsName() {
            return starsName;
        }
    }
    

    public StarsLMControlHistory getEventsByGroup(int customerAccountId, int lmGroupId, int inventoryId,
                                                  ControlPeriod period, YukonUserContext yukonUserContext);

    public ControlHistoryEvent getLastControlHistoryEntry(int customerAccountId,
                                                          Program program,
                                                          int inventoryId,
                                                          YukonUserContext yukonUserContext);
    
    /**
     * This method takes in an old stars control history object and generates the
     * a list of the ControlHistoryEvents, which we use in newer stars code.
     * 
     */
    public List<ControlHistoryEvent> toEventList(StarsLMControlHistory controlHistory);
 
    /**
     * This method removes any invalid control history in regards to enrollment.
     * 
     * @param controlHistory
     * @param holder
     */
    public void removeInvalidEnrollmentControlHistory(StarsLMControlHistory controlHistory,
                                                      int inventoryId,
                                                      int groupId);

}
