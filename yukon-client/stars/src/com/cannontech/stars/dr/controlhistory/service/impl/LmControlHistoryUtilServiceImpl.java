package com.cannontech.stars.dr.controlhistory.service.impl;

import java.util.Collections;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.TimeUtil;
import com.cannontech.stars.dr.controlhistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class LmControlHistoryUtilServiceImpl implements LmControlHistoryUtilService {

    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    
    public Duration calculateCurrentEnrollmentControlPeriod(ControlHistory controlHistory,
                                                            Duration controlHistoryTotal,
                                                            ReadableInstant controlHistoryStopDateTime,
                                                            Iterable<LMHardwareControlGroup> enrollments) {

        boolean neverEnrolledDuringThisPeriod = true;

        for (LMHardwareControlGroup enrollmentEntry : enrollments) {
            // The enrollment entry does not effect the control history event
            if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistoryStopDateTime) || 
                enrollmentEntry.getGroupEnrollStop() != null) {

                continue;

            } else {
                // period falls cleanly within the enrollment range, total
                // remains the same
                if (enrollmentEntry.getGroupEnrollStart().isBefore(controlHistory.getStartInstant()) && 
                    (enrollmentEntry.getGroupEnrollStop() == null || 
                     enrollmentEntry.getGroupEnrollStop().isAfter(controlHistoryStopDateTime))) {

                    neverEnrolledDuringThisPeriod = false;

                // The enrollment started after the beginning of the control history event,
                // subtract the difference and adjust the control history start time.
                } else if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistory.getStartInstant()) && 
                            enrollmentEntry.getGroupEnrollStart().isBefore(controlHistoryStopDateTime)) {

                    Duration priorEnrollmentDuration = 
                        new Duration(controlHistory.getStartInstant(), enrollmentEntry.getGroupEnrollStart());
                    controlHistoryTotal = controlHistoryTotal.minus(priorEnrollmentDuration);
                    
                    controlHistory.setStartInstant(enrollmentEntry.getGroupEnrollStart());
                    neverEnrolledDuringThisPeriod = false;

                }
            }
        }

        if (neverEnrolledDuringThisPeriod) {
            return Duration.ZERO;
        }

        return controlHistoryTotal;
    }

    public Duration calculatePreviousEnrollmentControlPeriod(ControlHistory controlHistory,
                                                             Duration controlHistoryTotal,
                                                             ReadableInstant controlHistoryStopDateTime,
                                                             Iterable<LMHardwareControlGroup> enrollments) {
        boolean neverEnrolledDuringThisPeriod = true;

        for (LMHardwareControlGroup enrollmentEntry : enrollments) {
            // The enrollment entry does not effect the control history event
            if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistoryStopDateTime) || 
                enrollmentEntry.getGroupEnrollStop() == null || 
                enrollmentEntry.getGroupEnrollStop().isBefore(controlHistory.getStartInstant())) {

                continue;

            } else {
                
                // The enrollment has been controlled during its whole duration
                if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistory.getStartInstant()) &&
                    enrollmentEntry.getGroupEnrollStop().isBefore(controlHistoryStopDateTime)) {
                    
                    controlHistory.setStartInstant(enrollmentEntry.getGroupEnrollStart());
                    Duration preControlDuration = 
                        new Duration(controlHistory.getStartInstant(), enrollmentEntry.getGroupEnrollStart());
                    controlHistoryTotal = controlHistoryTotal.minus(preControlDuration);
                    
                    Duration postEnrollmentDuration = 
                        new Duration(enrollmentEntry.getGroupEnrollStop(), controlHistoryStopDateTime);
                    controlHistoryTotal = controlHistoryTotal.minus(postEnrollmentDuration);
                    
                    neverEnrolledDuringThisPeriod = false;

                // The control history event falls cleanly within the enrollment range, 
                // the total remains the same.
                } else if (enrollmentEntry.getGroupEnrollStart().isBefore(controlHistory.getStartInstant()) && 
                    enrollmentEntry.getGroupEnrollStop().isAfter(controlHistoryStopDateTime)) {

                    neverEnrolledDuringThisPeriod = false;

                // The enrollment started after the beginning of the control history event,
                // subtract the difference and adjust the control history start time.
                } else if (enrollmentEntry.getGroupEnrollStart().isAfter(controlHistory.getStartInstant())) {

                    Duration priorEnrollmentDuration = 
                        new Duration(controlHistory.getStartInstant(), enrollmentEntry.getGroupEnrollStart());
                    controlHistoryTotal = controlHistoryTotal.minus(priorEnrollmentDuration);
                    
                    controlHistory.setStartInstant(enrollmentEntry.getGroupEnrollStart());
                    neverEnrolledDuringThisPeriod = false;

                // The enrollment stopped before the end of the control history event,
                // subtract the difference.
                } else if (enrollmentEntry.getGroupEnrollStop().isBefore(controlHistoryStopDateTime)) {
                    Duration postEnrollmentDuration = 
                        new Duration(enrollmentEntry.getGroupEnrollStop(), controlHistoryStopDateTime);
                    controlHistoryTotal = controlHistoryTotal.minus(postEnrollmentDuration);
                    neverEnrolledDuringThisPeriod = false;
                }
            }
        }

        if (neverEnrolledDuringThisPeriod) {
            return Duration.ZERO;
        }

        return controlHistoryTotal;
    }

    @Override
    public Duration calculateOptOutControlHistory(ControlHistory controlHistory,
                                                  Duration controlHistoryTotal,
                                                  ReadableInstant controlHistoryStopDateTime,
                                                  Iterable<LMHardwareControlGroup> optOuts) {
        for (LMHardwareControlGroup optOutEntry : optOuts) {
            // Control history event occurred entirely during an opt out. Discard it.
            if (optOutEntry.getOptOutStart().isBefore(controlHistory.getStartInstant()) && 
                (optOutEntry.getOptOutStop() == null || 
                 optOutEntry.getOptOutStop().isAfter(controlHistoryStopDateTime))) {
                return Duration.ZERO;
            }

            // An opt out started during control, but did not end until the control history
            // event had ended.  Subtract the difference of opt out start and the period's
            // stop from duration.
            else if (optOutEntry.getOptOutStart().isAfter(controlHistory.getStartInstant()) && 
                      optOutEntry.getOptOutStart().isBefore(controlHistoryStopDateTime) && 
                      (optOutEntry.getOptOutStop() == null || 
                       optOutEntry.getOptOutStop().isAfter(controlHistoryStopDateTime))) {

                Duration priorOptOutDuration = 
                    new Duration(optOutEntry.getOptOutStart(), controlHistoryStopDateTime);
                controlHistory.setCurrentlyControlling(false);
                controlHistoryTotal = controlHistoryTotal.minus(priorOptOutDuration);
            }

            // Control occurred during an already active opt out. That opt out
            // then ended before control was complete. Subtract the difference
            // of opt out stop and period start and also update the control history event 
            // start time.
            else if (optOutEntry.getOptOutStart().isBefore(controlHistory.getStartInstant()) && 
                      optOutEntry.getOptOutStop() != null && 
                      optOutEntry.getOptOutStop().isAfter(controlHistory.getStartInstant()) && 
                      optOutEntry.getOptOutStop().isBefore(controlHistoryStopDateTime)) {

                Duration postOptOutDuration = 
                    new Duration(controlHistory.getStartInstant(), optOutEntry.getOptOutStop());
                controlHistoryTotal = controlHistoryTotal.minus(postOptOutDuration);
                
                controlHistory.setStartInstant(optOutEntry.getOptOutStop());
            }

            // An opt out occurred completely in the middle of a control period.
            // Subtract the entire opt out duration from the control history
            // total.
            else if (optOutEntry.getOptOutStart().isAfter(controlHistory.getStartInstant()) && 
                      optOutEntry.getOptOutStop() != null && 
                      optOutEntry.getOptOutStop().isBefore(controlHistoryStopDateTime)) {

                Duration postOptOut = 
                    new Duration(optOutEntry.getOptOutStart(), optOutEntry.getOptOutStop());
                controlHistoryTotal = controlHistoryTotal.minus(postOptOut);
            }
        }
        
        return controlHistoryTotal;
    }
    
    @Override
    public List<Interval> getControHistoryEnrollmentIntervals(ControlHistory controlHistory,
                                                              int accountId,
                                                              int inventoryId,
                                                              int loadGroupId){
        // Build up a sington list from the control history event.
        Interval controlHistoryInterval = 
            new Interval(controlHistory.getStartInstant(), controlHistory.getStopInstant());
        List<Interval> controlHistoryIntervalList = Collections.singletonList(controlHistoryInterval);
        
        // Get the enrollments that intersect with the supplied control history
        List<LMHardwareControlGroup> enrollments = 
            lmHardwareControlGroupDao.getIntersectingEnrollments(accountId,
                                                                 inventoryId,
                                                                 loadGroupId,
                                                                 controlHistoryInterval);
     
        // Get a list of intervales from the list of enrollments
        List<Interval> enrollmentIntervals = 
            Lists.transform(enrollments, new Function<LMHardwareControlGroup, Interval>() {

                @Override
                public Interval apply(LMHardwareControlGroup lmHardwareControlGroup) {
                    return lmHardwareControlGroup.getInterval();
                }
            
            });
        
        List<Interval> enrollmentControlHistoryList = 
            TimeUtil.getOverlap(controlHistoryIntervalList, enrollmentIntervals);
        
        return enrollmentControlHistoryList;
    }

    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
    
}