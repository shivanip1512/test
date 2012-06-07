package com.cannontech.stars.dr.controlHistory.service;

import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

import com.cannontech.common.util.OpenInterval;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsLMControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ObservedControlHistory;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.util.model.CustomerControlTotals;
import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;

public interface LmControlHistoryUtilService {


    /**
     * This method generates control history information that is specific to the supplied account, 
     * load group, and piece of inventory.  It also takes into effect current enrollments and opt outs
     * when figuring out this data.
     * @param past 
     */
    public ObservedControlHistory getObservedControlHistory(int groupID, int inventoryId,
                                                            int accountId, StarsCtrlHistPeriod period, 
                                                            DateTimeZone tz, LiteYukonUser currentUser, 
                                                            boolean past);
    
    /**
     * This method takes care of building up control history entries from the database control history
     * entries, which are gathered from LMControlHistory.  The startDate value is used as a truncating
     * point.  Any entry that lands on this line will be added to the control history entries list,
     * but will only contain the piece that occurred after the start date.
     */
    public StarsLMControlHistory buildStarsControlHistoryForPeriod(LiteStarsLMControlHistory liteCtrlHist, 
                                                                   ReadableInstant startInstant, ReadableInstant stopInstant,
                                                                   DateTimeZone tz);

    /**
     * This method takes an observed control history object and creates a legacy STARS control history
     * object.  
     */
    @Deprecated
    public StarsLMControlHistory getStarsLmControlHistory(ObservedControlHistory observedControlHistory, 
                                                          StarsCtrlHistPeriod period, DateTimeZone tz);

    /**
     * This method takes the list of observed control history entries and calculates the 
     * three standard control history summary values.  These values include past day, past month,
     * and past year.
     */
    public ControlSummary getControlSummary(ObservedControlHistory observedControlHistory, DateTimeZone tz);
    
    
    /**
     * The method returns an intersection of enrollments for a piece of inventory on an account
     * and the control history supplied.  These results can then be used to calculate account
     * specific control history.
     * 
     * Ex.
     * Control History               [------------]
     * Enrollments                [-----] [----] [-----]
     * Resulting Control History     [--] [----] []
     * 
     */
    List<OpenInterval> getControHistoryEnrollmentIntervals(ControlHistoryEntry controlHistoryEntry,
                                                           int accountId,
                                                           int inventoryId,
                                                           int loadGroupId);


    // Report Control History Methods.
    /**
     * This method should be general enough that it can be used for both the LMControlDetail report
     * and the LMControlSummary report
     */
    public CustomerControlTotals calculateCumulativeCustomerControlValues(StarsLMControlHistory starsCtrlHist, 
                                                                          ReadableInstant startDateTime, 
                                                                          ReadableInstant stopDateTime, 
                                                                          List<LMHardwareControlGroup> enrollments, 
                                                                          List<LMHardwareControlGroup> optOuts);

}