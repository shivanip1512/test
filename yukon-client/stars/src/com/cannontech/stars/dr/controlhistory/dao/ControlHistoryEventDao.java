package com.cannontech.stars.dr.controlhistory.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
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
    
    /**
     * 
     * @param customerAccountId
     * @param programId
     * @param period
     * @param yukonUserContext
     * @return - a Map of String (DisplayName) to List of ControlHistoryEvent's  
     */
    public Map<String, List<ControlHistoryEvent>> getEventsByProgram(int customerAccountId, int programId,
        ControlPeriod period, YukonUserContext yukonUserContext);
    
    public List<ControlHistoryEvent> toEventList(StarsLMControlHistory controlHistory);
    
}
