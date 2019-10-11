package com.cannontech.stars.dr.controlHistory.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.MutableDuration;
import com.cannontech.common.util.OpenInterval;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteLMControlHistory;
import com.cannontech.stars.database.data.lite.LiteStarsLMControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ActiveRestoreEnum;
import com.cannontech.stars.dr.controlHistory.model.ObservedControlHistory;
import com.cannontech.stars.dr.controlHistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.model.CustomerControlTotals;
import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LmControlHistoryUtilServiceImpl implements LmControlHistoryUtilService {
    private static final Logger log = YukonLogManager.getLogger(LmControlHistoryUtilServiceImpl.class);

    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private StarsDatabaseCache starsDatabaseCache;

    public ObservedControlHistory getObservedControlHistory(int loadGroupId, int inventoryId, int accountId, 
                                                           StarsCtrlHistPeriod period, DateTimeZone tz, 
                                                           LiteYukonUser currentUser, boolean past) {

        DateTime startDate = LMControlHistoryUtil.getPeriodStartTime( period, tz );

        // Get database control history entries and build up the control history object for the period.
        LiteStarsLMControlHistory liteCtrlHist = getControlHistory(loadGroupId, period, tz);
        StarsLMControlHistory starsCtrlHist = buildStarsControlHistoryForPeriod(liteCtrlHist, startDate, null, tz);

        //New enrollment, opt out, and control history tracking
        List<LMHardwareControlGroup> enrollments = null;
        if (past) {
            enrollments = lmHardwareControlGroupDao.getForPastEnrollments(accountId, inventoryId, loadGroupId);
        } else {
            enrollments = lmHardwareControlGroupDao.getForActiveEnrollments(accountId, inventoryId, loadGroupId);
        }
            
        List<LMHardwareControlGroup> optOuts = 
            lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId, loadGroupId, accountId, 
                                                                                    LMHardwareControlGroup.OPT_OUT_ENTRY);

        // Get the observable control history entries
        ObservedControlHistory observedControlHistory = 
            determineObservedControlHistory(starsCtrlHist, enrollments, optOuts);

        return observedControlHistory;
    }

    /**
     * This method builds up a list of database control history entries from the starsDatabaseCache.
     * In the future it might be better to get this through a dao call that just hits the database.
     */
    private LiteStarsLMControlHistory getControlHistory(int loadGroupId, StarsCtrlHistPeriod period, DateTimeZone tz) {
        LiteStarsLMControlHistory liteCtrlHist;
        
        if (period.getType() == StarsCtrlHistPeriod.ALL_TYPE) {
            liteCtrlHist = starsDatabaseCache.getLMControlHistory( loadGroupId, new DateTime(0) );

        /* Try to help performance a little bit here if we can.  
         * loading a year is better than loading all entries if we don't need to.*/
        } else {
            DateTime oneYearAgoDate = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTYEAR, tz );
            liteCtrlHist = starsDatabaseCache.getLMControlHistory( loadGroupId, oneYearAgoDate );
        }
        
        LMControlHistoryUtil.addToActiveControlHistory( liteCtrlHist );
        
        return liteCtrlHist;
    }
    
    /**
     * This method takes care of building up control history entries from the database control history
     * entries, which are gathered from LMControlHistory.  The startDate value is used as a truncating
     * point.  Any entry that lands on this line will be added to the control history entries list,
     * but will only contain the piece that occurred after the start date.
     */
    public StarsLMControlHistory buildStarsControlHistoryForPeriod(LiteStarsLMControlHistory liteCtrlHist, 
                                                                   ReadableInstant startInstant, ReadableInstant stopInstant,
                                                                   DateTimeZone dateTimeZone) {

        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();
        DateTime periodStartDateTime = startInstant.toInstant().toDateTime(dateTimeZone);

        Instant now = new Instant();
        
        ControlHistoryEntry hist = null;
        Instant lastStartTime = null;
        Instant histStartDate = null;
        
        boolean validFirstEntry = false;

        
        for (int i = 0; i < liteCtrlHist.getLmControlHistory().size(); i++) {
            LiteLMControlHistory lmCtrlHist = (LiteLMControlHistory) liteCtrlHist.getLmControlHistory().get(i);
            
            // Runs through the list of control history entries supplied until 
            // it finds the first entry that belongs in our control history list 
            if (!validFirstEntry) {
                validFirstEntry = lmCtrlHist.getStopDateInstant().isAfter(startInstant);
            }
            
            if (!validFirstEntry) {
                continue;
            }
                
            Instant controlStartDate = new Instant(lmCtrlHist.getStartDateTime());
            ActiveRestoreEnum controlHistoryActiveRestore = 
                ActiveRestoreEnum.getActiveRestoreByDatabaseRepresentation(lmCtrlHist.getActiveRestore());
            
            Instant lmCtrlHistStart = lmCtrlHist.getStartDateInstant();
            Instant lmCtrlHistStop = lmCtrlHist.getStopDateInstant();
            
            if (ActiveRestoreEnum.getActiveRestoreStartEntries().contains(controlHistoryActiveRestore)) {
                // Clean up any existing control entries before creating a new entry.
                if (hist != null) {
                    hist.setControlDuration(new Duration(histStartDate, lmCtrlHist.getStartDateInstant())); 
                    hist.setCurrentlyControlling(false);
                }
                
                // This is a new control
                hist = new ControlHistoryEntry();
                if (!StarsUtils.isReadableInsantEqual(lmCtrlHistStart, lastStartTime)) {
                    lastStartTime = lmCtrlHistStart;

                    if (StarsUtils.isReadableInsantBefore(lmCtrlHistStart, periodStartDateTime)) {
                        histStartDate = periodStartDateTime.toInstant();
                    } else {
                        histStartDate = lmCtrlHistStart;
                    }
                    
                    hist.setStartInstant(histStartDate);
                    starsCtrlHist.addControlHistory( hist );
                }

                hist.setCurrentlyControlling(true);

            } else if (ActiveRestoreEnum.CONTINUE_CONTROL.equals(controlHistoryActiveRestore) ||
                        ActiveRestoreEnum.LOG_TIMER.equals(controlHistoryActiveRestore)) {

                
                // This is a new control
                if (hist == null && 
                    StarsUtils.isReadableInsantBefore(lmCtrlHistStart, periodStartDateTime)) {
                    lastStartTime = lmCtrlHistStart;

                    histStartDate = periodStartDateTime.toInstant();
                    
                    hist = new ControlHistoryEntry();
                    hist.setStartInstant(histStartDate);
                    starsCtrlHist.addControlHistory( hist );
                }

            // Ending control history event.  End any active control history event and calculate the duration of that event. 
            } else if ((ActiveRestoreEnum.getActiveRestoreStopEntries().contains(controlHistoryActiveRestore))) {

                  if (hist != null) {
                      hist.setCurrentlyControlling(false);
                      if (controlStartDate.equals(lastStartTime)) {
                          Duration controlHistoryDuration = new Duration(histStartDate,lmCtrlHistStop);
        
                          hist.setControlDuration(controlHistoryDuration);
                      }
                      if (lmCtrlHistStop.isBefore(histStartDate)) {
                          starsCtrlHist.removeControlHistory(hist);
                      }
                  }

                // Remember, this is a reference to what is already in starsCtrlHist's list.  
                // He's just nulling out the reference
                hist = null;
            }
        }

        // Check and see if the load group is currently being controlled.  If it is, figure out the
        // current control duration as of right now.
        for (ControlHistoryEntry controlHistory : starsCtrlHist.getControlHistory()) {
            if (controlHistory.isCurrentlyControlling()) {
                starsCtrlHist.setBeingControlled(true);
                Duration currentDuration = new  Duration(controlHistory.getStartInstant(), now);
                controlHistory.setControlDuration(currentDuration);
            }
        }
        
        return starsCtrlHist;
    }
    
    /**
     * This method uses a list of generic load group control history, with a user's enrollments
     * and opt outs, to create user specific control history entries.
     */
    private static ObservedControlHistory determineObservedControlHistory(StarsLMControlHistory unadjustedCtrlHist,
                                                                          List<LMHardwareControlGroup> enrollments,
                                                                          List<LMHardwareControlGroup> optOuts) {
         ObservedControlHistory result = new ObservedControlHistory();

         for (ControlHistoryEntry cntrlHist : unadjustedCtrlHist.getControlHistoryList()) {
             List<ControlHistoryEntry> observedEntries = 
                 calculateRealControlPeriodTime(cntrlHist, enrollments, optOuts);

             result.addAllControlHistoryEntries(observedEntries);

         }

         return result;
    }
    
    /**
     * This method uses a generic load group control history entry, with a user's enrollments
     * and opt outs, to create user specific control history entries.
     */
    private static List<ControlHistoryEntry> calculateRealControlPeriodTime(ControlHistoryEntry controlHistory,
                                                                            List<LMHardwareControlGroup> enrollments,
                                                                            List<LMHardwareControlGroup> optOuts) {

        List<ControlHistoryEntry> result = Lists.newArrayList();

        // Calculate ControlHIstory off of current enrollments
        List<ControlHistoryEntry> enrolledControlHistoryEntries = calculateEnrollmentControlPeriod(controlHistory, enrollments);


        // Clean up control history that wasn't counted due to opt outs.
        for (ControlHistoryEntry enrolledControlHistoryEntry : enrolledControlHistoryEntries) {

            List<ControlHistoryEntry> optedOutControlHistoryEntries = 
                calculateOptOutControlHistory(enrolledControlHistoryEntry, optOuts);
            result.addAll(optedOutControlHistoryEntries);
        }

        return result;
    }

    /**
     * The method calculates the actual control history entries for the supplied control history entry by 
     * taking into effect the supplied enrollments.
     */
    public static List<ControlHistoryEntry> calculateEnrollmentControlPeriod(ControlHistoryEntry controlHistoryEntry,
                                                                               Iterable<LMHardwareControlGroup> enrollments) {

        List<ControlHistoryEntry> result = Lists.newArrayListWithExpectedSize(1);

        for (LMHardwareControlGroup enrollmentEntry : enrollments) {
            
            OpenInterval enrollmentInterval = enrollmentEntry.getEnrollmentInterval();
            OpenInterval overlap = enrollmentInterval.overlap(controlHistoryEntry.getOpenInterval());
            if (overlap != null) {
                if (controlHistoryEntry.getOpenInterval().equals(overlap)) {
                    result.add(controlHistoryEntry);
                    break;
                } else {
                    ControlHistoryEntry newEntry = new ControlHistoryEntry(overlap);
                    result.add(newEntry);
                }
            }
        }

        return result;
    }

    /**
     * The method calculates the actual control history entries for the supplied control history entry by 
     * taking into effect current opt outs.  This method should be used in conjunction with 
     * calculateCurrentEnrollmentControlPeriod
     */
    public static List<ControlHistoryEntry> calculateOptOutControlHistory(ControlHistoryEntry controlHistory,
                                                                          Iterable<LMHardwareControlGroup> optOuts) {

        List<List<OpenInterval>> temp = Lists.newArrayListWithExpectedSize(Iterables.size(optOuts) * 2); // better method in 5.2
        for (LMHardwareControlGroup optOutEntry : optOuts) {
            OpenInterval optOutInterval = optOutEntry.getOptOutInterval();
            // Checks if the Stop is before the Start
            if(optOutInterval.isBefore(optOutInterval)) {
                log.error("Bad OptOut interval, stop time is before start time " + optOutInterval);
            }
            else {
                List<OpenInterval> optInInterval = optOutInterval.invert();
                temp.add(optInInterval);
            }
        }
        
        List<OpenInterval> controlInterval = ImmutableList.of(controlHistory.getOpenInterval());
        temp.add(controlInterval);
        
        List<OpenInterval> intersection = OpenInterval.intersection(temp);
        
        List<ControlHistoryEntry> result = Lists.newArrayListWithCapacity(intersection.size());
        for (OpenInterval openInterval : intersection) {
            // Checks if the Stop is before the Start
            if(openInterval.isBefore(openInterval)) {
                log.error("Bad Control interval, stop time is before start time " + openInterval);
            }
            else {
                ControlHistoryEntry controlHistoryEntry = new ControlHistoryEntry(openInterval);
                result.add(controlHistoryEntry);
            }
        }
        
        
        return result;
    }        
    
    /**
     * This method takes the list of observed control history entries and calculates the 
     * three standard control history summary values.  These values include past day, past month,
     * and past year.
     */
    @Override
    public ControlSummary getControlSummary(ObservedControlHistory observedControlHistory, DateTimeZone tz) {
        ControlSummary summary = new ControlSummary();

        // Past year summary
        DateTime yearPeriodStartTime = LMControlHistoryUtil.getPeriodStartTime(StarsCtrlHistPeriod.PASTYEAR, tz);
        Duration pastYearControlDuration = 
            calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), yearPeriodStartTime);
        summary.setAnnualTime(pastYearControlDuration);

        // Past month summary
        DateTime monthPeriodStartTime = LMControlHistoryUtil.getPeriodStartTime(StarsCtrlHistPeriod.PASTMONTH, tz);
        Duration pastMonthControlDuration = 
            calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), monthPeriodStartTime);
        summary.setMonthlyTime(pastMonthControlDuration);

        // Past day summary
        DateTime datePeriodStartTime = LMControlHistoryUtil.getPeriodStartTime(StarsCtrlHistPeriod.PASTDAY, tz);
        Duration pastDayControlDuration = 
            calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), datePeriodStartTime);
        summary.setDailyTime(pastDayControlDuration);

        return summary;

    }

    public static Duration calculateSummaryControlValueForPeriod(List<ControlHistoryEntry> observedControlHistoryEntryList,
                                                                 ReadableInstant periodStartInstant) {

        Duration controlSummaryDuration = Duration.ZERO;
        OpenInterval viewableInterval = OpenInterval.createOpenEnd(periodStartInstant);
        
        for (ControlHistoryEntry controlHistoryEntry : observedControlHistoryEntryList) {
            
            // The control history entry does not land in the control period
            if (!viewableInterval.overlaps(controlHistoryEntry.getOpenInterval())) {
                continue;
            }

            // The control history entry duration for the viewable period;
            OpenInterval viewableControlHistory = viewableInterval.overlap(controlHistoryEntry.getOpenInterval());

            Duration currentDuration = viewableControlHistory.withCurrentEndIfOpen().toClosedInterval().toDuration();
            controlSummaryDuration = controlSummaryDuration.plus(currentDuration);
        }

        return controlSummaryDuration;
    }
    
    @Deprecated
    public StarsLMControlHistory getStarsLmControlHistory(ObservedControlHistory observedControlHistory, 
                                                          StarsCtrlHistPeriod period, 
                                                          DateTimeZone tz) {
        StarsLMControlHistory starsLMControlHistory = new StarsLMControlHistory();
        for (ControlHistoryEntry entry : observedControlHistory.getControlHistoryList()) {
            starsLMControlHistory.addControlHistory(entry);
        }
        
        starsLMControlHistory.setBeingControlled(observedControlHistory.isBeingControlled());
        starsLMControlHistory.setControlSummary(getControlSummary(observedControlHistory, tz));

        return starsLMControlHistory;
    }

    
    @Override
    public List<OpenInterval> getControHistoryEnrollmentIntervals(ControlHistoryEntry controlHistoryEntry,
                                                              int accountId,
                                                              int inventoryId,
                                                              int loadGroupId){
        // Build up a sington list from the control history event.
        OpenInterval controlHistoryInterval = controlHistoryEntry.getOpenInterval();
        List<OpenInterval> controlHistoryIntervalList = Collections.singletonList(controlHistoryInterval);
        
        // Get the enrollments that intersect with the supplied control history
        List<LMHardwareControlGroup> enrollments = 
            lmHardwareControlGroupDao.getIntersectingEnrollments(accountId,
                                                                 inventoryId,
                                                                 loadGroupId,
                                                                 controlHistoryInterval);
     
        // Get a list of intervales from the list of enrollments
        List<OpenInterval> enrollmentIntervals = 
            Lists.transform(enrollments, new Function<LMHardwareControlGroup, OpenInterval>() {

                @Override
                public OpenInterval apply(LMHardwareControlGroup lmHardwareControlGroup) {
                    return lmHardwareControlGroup.getEnrollmentInterval();
                }
            
            });
        
        List<OpenInterval> enrollmentControlHistoryList = 
            TimeUtil.getOverlap(controlHistoryIntervalList, enrollmentIntervals);
        
        return enrollmentControlHistoryList;
    }

    public CustomerControlTotals calculateCumulativeCustomerControlValues(StarsLMControlHistory starsCtrlHist, 
                                                                          ReadableInstant startDateTime, 
                                                                          ReadableInstant stopDateTime, 
                                                                          List<LMHardwareControlGroup> enrollments, 
                                                                          List<LMHardwareControlGroup> optOuts) {
        
        MutableDuration mutableTotalControlTime = new MutableDuration(0);
        MutableDuration mutableTotalControlDuringOptOutTime = new MutableDuration(0);
        
        for(int j = 0; j < starsCtrlHist.getControlHistoryCount(); j++) {
            ControlHistoryEntry controlHistoryEntry = starsCtrlHist.getControlHistory(j);
            
            List<ControlHistoryEntry> enrolledControlHistoryEntries = 
                LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistoryEntry, enrollments);
            
            calculateOptOutControlHistorySummary(mutableTotalControlTime, mutableTotalControlDuringOptOutTime,
                                                 startDateTime, stopDateTime,
                                                 enrolledControlHistoryEntries, optOuts);
            
        }
        
        // Getting the opt out counts and the overall opt out duration
        Duration totalOptedOutDuration = Duration.ZERO;
        int optOutCount = 0;
        OpenInterval summaryInterval = OpenInterval.createClosed(startDateTime, stopDateTime);

        for(LMHardwareControlGroup optOut : optOuts) {
            OpenInterval summaryOptOutInterval = optOut.getOptOutInterval().overlap(summaryInterval);
            Duration summaryBasedOptOutDuration = summaryOptOutInterval.toClosedInterval().toDuration();

            optOutCount++;
            totalOptedOutDuration = totalOptedOutDuration.plus(summaryBasedOptOutDuration);
        }

        CustomerControlTotals totals = new CustomerControlTotals();
        totals.setTotalControlTime(mutableTotalControlTime.toDuration());
        totals.setTotalControlDuringOptOutTime(mutableTotalControlDuringOptOutTime.toDuration());
        totals.setTotalOptOutTime(totalOptedOutDuration);
        totals.setTotalOptOutEvents(optOutCount);
        
        return totals;
    }
    
    /**
     * This method gets the total durations of the actual control, the amount of control 
     * that avoided due to opt outs, how many opt outs were used, and how many hours the inventory
     * was opted out for.
     */
    private static void calculateOptOutControlHistorySummary(MutableDuration mutableTotalControlTime,
                                                             MutableDuration mutableTotalControlDuringOptOutTime,
                                                             ReadableInstant startDateTime, 
                                                             ReadableInstant stopDateTime, 
                                                             List<ControlHistoryEntry> enrolledControlHistoryEntries,
                                                             List<LMHardwareControlGroup> optOuts) {

        // Calculates the total control duration for the set of enrolled control history.
        Duration projectedControlHistoryDuration = Duration.ZERO;
        for (ControlHistoryEntry enrolledControlHistoryEntry : enrolledControlHistoryEntries) {
            projectedControlHistoryDuration = projectedControlHistoryDuration.plus(enrolledControlHistoryEntry.getControlDuration());
        }

        Duration totalOptedOutControlDuration = Duration.ZERO;
        OpenInterval openStartToNow = OpenInterval.createOpenStart(new Instant());
        OpenInterval summaryInterval = OpenInterval.createClosed(startDateTime, stopDateTime);
        
        for (LMHardwareControlGroup optOut : optOuts) {

            // Get the portion of the opt out that occurred during our time period.
            OpenInterval summaryOptOutInterval = optOut.getOptOutInterval().overlap(summaryInterval);
            Duration optedOutDuration = Duration.ZERO;
            
            for (ControlHistoryEntry enrolledControlHistoryEntry : enrolledControlHistoryEntries) {
                OpenInterval optedOutControlHistoryInterval = 
                    summaryOptOutInterval.overlap(enrolledControlHistoryEntry.getOpenInterval());
                
                if (optedOutControlHistoryInterval != null) {
                    OpenInterval reportableOptOutControlHistoryInterval = optedOutControlHistoryInterval.overlap(openStartToNow);
                    optedOutDuration = optedOutDuration.plus(reportableOptOutControlHistoryInterval.toClosedInterval().toDuration());
                }
            }
            
            totalOptedOutControlDuration = totalOptedOutControlDuration.plus(optedOutDuration);
        }
        
        mutableTotalControlTime.plus(projectedControlHistoryDuration.minus(totalOptedOutControlDuration));
        mutableTotalControlDuringOptOutTime.plus(totalOptedOutControlDuration);
        
    }
    
    // DI Setters
    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}