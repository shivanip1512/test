package com.cannontech.stars.dr.controlhistory.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.xml.serialize.ControlHistory;

public interface LmControlHistoryUtilService {


    /**
     * The method calculates the correct duration for a control history event by 
     * taking into effect current enrollments.  It also modifies the start date of the control
     * history event if the control was delayed due to enrollment starting during a control
     * history event.
     * 
     */
    public Duration calculateCurrentEnrollmentControlPeriod(ControlHistory controlHistory,
                                                            Duration controlHistoryTotal,
                                                            ReadableInstant controlHistoryStopDateTime,
                                                            Iterable<LMHardwareControlGroup> enrollments);

    /**
     * The method calculates the correct duration for a control history event by 
     * taking into effect previous enrollments.  It also modifies the start date of the control
     * history event if the control was delayed due to enrollment starting during a control
     * history event.
     */
    public Duration calculatePreviousEnrollmentControlPeriod(ControlHistory controlHistory,
                                                             Duration controlHistoryTotal,
                                                             ReadableInstant controlHistoryStopDateTime,
                                                             Iterable<LMHardwareControlGroup> enrollments);

    /**
     * The method calculates the correct duration for a control history event by 
     * taking into effect opt outs during that event. It also modifies the start date of the control
     * history event if the control was delayed due to an active opt out that was canceled during
     * a control history event.
     */
    public Duration calculateOptOutControlHistory(ControlHistory controlHistory,
                                                  Duration controlHistoryTotal,
                                                  ReadableInstant controlHistoryStopDateTime,
                                                  Iterable<LMHardwareControlGroup> optOuts);

    
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
    List<Interval> getControHistoryEnrollmentIntervals(ControlHistory controlHistory,
                                                       int accountId,
                                                       int inventoryId,
                                                       int loadGroupId);

}