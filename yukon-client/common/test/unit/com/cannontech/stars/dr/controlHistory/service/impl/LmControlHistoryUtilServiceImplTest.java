package com.cannontech.stars.dr.controlHistory.service.impl;

import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.stars.database.data.lite.LiteLMControlHistory;
import com.cannontech.stars.database.data.lite.LiteStarsLMControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ActiveRestoreEnum;
import com.cannontech.stars.dr.controlHistory.model.ObservedControlHistory;
import com.cannontech.stars.dr.controlHistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.google.common.collect.Lists;

public class LmControlHistoryUtilServiceImplTest {
    private static final DateTimeFormatter dateTimeFormmater = 
        DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(DateTimeZone.forOffsetHoursMinutes(5, 0));
    private final LmControlHistoryUtilService service = new LmControlHistoryUtilServiceImpl();
    private final DateTimeZone timeZone = DateTimeZone.forTimeZone(CtiUtilities.getValidTimeZone("CST"));
    
    // Control History Entries
    ControlHistoryEntry controlHistoryOne;
    ControlHistoryEntry controlHistoryTwo;
    ControlHistoryEntry controlHistoryThree;
    ControlHistoryEntry controlHistoryFour;
    ControlHistoryEntry controlHistoryFive;
    ControlHistoryEntry controlHistorySix;
    
    ControlHistoryEntry controlHistoryOvernightOne;
    {
        controlHistoryOne = createControlHistoryEntry("01/01/2010 04:00:00", Duration.standardHours(4), false);
        controlHistoryTwo = createControlHistoryEntry("01/01/2010 06:30:00", Duration.standardHours(3), false);
        controlHistoryThree = createControlHistoryEntry("01/01/2010 02:30:00", Duration.standardHours(4), false);
        controlHistoryFour = createControlHistoryEntry("01/01/2010 01:00:00", Duration.standardHours(11), false);
        controlHistoryFive = createControlHistoryEntry("01/01/2010 10:00:00", Duration.standardMinutes(30), false);
        controlHistorySix = createControlHistoryEntry("01/01/2010 01:00:00", Duration.standardMinutes(45), false);
        controlHistoryOvernightOne = createControlHistoryEntry("02/01/2010 22:00:00", Duration.standardHours(5), false);
    }
    
    // Enrollment Entries
    LMHardwareControlGroup enrollmentOne;
    LMHardwareControlGroup enrollmentTwo;
    LMHardwareControlGroup enrollmentThree;
    {
        enrollmentOne = new LMHardwareControlGroup();
        enrollmentOne.setGroupEnrollStart(dateTimeFormmater.parseDateTime("01/01/2010 03:30:00").toInstant());
        
        enrollmentTwo = new LMHardwareControlGroup();
        enrollmentTwo.setGroupEnrollStart(dateTimeFormmater.parseDateTime("01/01/2010 03:30:00").toInstant());
        enrollmentTwo.setGroupEnrollStop(dateTimeFormmater.parseDateTime("01/01/2010 08:30:00").toInstant());

        enrollmentThree = new LMHardwareControlGroup();
        enrollmentThree.setGroupEnrollStart(dateTimeFormmater.parseDateTime("02/01/2010 03:30:00").toInstant());
    }
    
    // Opt Out Entries
    LMHardwareControlGroup optOutOne;
    LMHardwareControlGroup optOutTwo;
    {
        optOutOne = new LMHardwareControlGroup();
        optOutOne.setOptOutStart(dateTimeFormmater.parseDateTime("01/01/2010 03:30:00").toInstant());
        
        optOutTwo = new LMHardwareControlGroup();
        optOutTwo.setOptOutStart(dateTimeFormmater.parseDateTime("01/01/2010 03:30:00").toInstant());
        optOutTwo.setOptOutStop(dateTimeFormmater.parseDateTime("01/01/2010 08:30:00").toInstant());
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    // This control duration is for control that started before midnight and continue into the next day.
    // This control was then stopped before it actually reached the next day. 
    LiteLMControlHistory overnightContHistStoppedBeforeOvernightControlOne;
    LiteLMControlHistory overnightContHistStoppedBeforeOvernightControlTwo;
    LiteLMControlHistory overnightContHistStoppedBeforeOvernightControlThree;
    {
        overnightContHistStoppedBeforeOvernightControlOne = createLiteLMControlHistory("01/01/2010 22:30:00", "01/02/2010 02:30:00", ActiveRestoreEnum.NEW_CONTROL);
        overnightContHistStoppedBeforeOvernightControlTwo = createLiteLMControlHistory("01/01/2010 22:30:00", "01/01/2010 23:30:00", ActiveRestoreEnum.MANUAL_RESTORE);
        overnightContHistStoppedBeforeOvernightControlThree = createLiteLMControlHistory("01/01/2010 23:30:00", "01/01/2010 23:30:00", ActiveRestoreEnum.MANUAL_RESTORE);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    // This control duration is for control that was started and stopped in the same day.  
    // It also includes continuations to keep the control going. 
    LiteLMControlHistory regularContHistWithContuationsOne;
    LiteLMControlHistory regularContHistWithContuationsTwo;
    LiteLMControlHistory regularContHistWithContuationsThree;
    LiteLMControlHistory regularContHistWithContuationsFour;
    LiteLMControlHistory regularContHistWithContuationsFive;
    LiteLMControlHistory regularContHistWithContuationsSix;
    {
        regularContHistWithContuationsOne = createLiteLMControlHistory("01/02/2010 05:30:00", "01/02/2010 06:30:00", ActiveRestoreEnum.NEW_CONTROL);
        regularContHistWithContuationsTwo = createLiteLMControlHistory("01/02/2010 05:30:00", "01/02/2010 06:00:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsThree = createLiteLMControlHistory("01/02/2010 05:30:00", "01/02/2010 06:30:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsFour = createLiteLMControlHistory("01/02/2010 05:30:00", "01/02/2010 07:00:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsFive = createLiteLMControlHistory("01/02/2010 05:30:00", "01/02/2010 07:30:00", ActiveRestoreEnum.MANUAL_RESTORE);
        regularContHistWithContuationsSix = createLiteLMControlHistory("01/02/2010 07:30:00", "01/02/2010 07:30:00", ActiveRestoreEnum.MANUAL_RESTORE);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    // This control duration is for control that was scheduled to start and stop in the same day,
    // but used continuations to extend the control overnight into the next day.
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayOne;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayTwo;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayThree;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayFour;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayFive;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDaySix;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDaySeven;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayEight;
    LiteLMControlHistory regularContHistWithContuationsThatSpanIntoTheNextDayNine;
    {
        regularContHistWithContuationsThatSpanIntoTheNextDayOne = createLiteLMControlHistory("01/03/2010 05:30:00", "01/03/2010 07:30:00", ActiveRestoreEnum.NEW_CONTROL);
        regularContHistWithContuationsThatSpanIntoTheNextDayTwo = createLiteLMControlHistory("01/03/2010 05:30:00", "01/03/2010 10:30:00", ActiveRestoreEnum.PERIOD_TRANSITION);
        regularContHistWithContuationsThatSpanIntoTheNextDayThree = createLiteLMControlHistory("01/03/2010 05:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsThatSpanIntoTheNextDayFour = createLiteLMControlHistory("01/03/2010 05:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsThatSpanIntoTheNextDayFive = createLiteLMControlHistory("01/03/2010 05:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsThatSpanIntoTheNextDaySix = createLiteLMControlHistory("01/03/2010 05:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsThatSpanIntoTheNextDaySeven = createLiteLMControlHistory("01/03/2010 05:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        regularContHistWithContuationsThatSpanIntoTheNextDayEight = createLiteLMControlHistory("01/03/2010 05:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.MANUAL_RESTORE);
        regularContHistWithContuationsThatSpanIntoTheNextDayNine = createLiteLMControlHistory("01/04/2010 03:30:00", "01/04/2010 03:30:00", ActiveRestoreEnum.MANUAL_RESTORE);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    // This control duration is for control that was scheduled to start and stop in the same day.
    // This control was then overridden by another control event that caused the current control to
    // stop and start a new control duration..
    LiteLMControlHistory gearChangedContHistOne;
    LiteLMControlHistory gearChangedContHistTwo;
    LiteLMControlHistory gearChangedContHistThree;
    LiteLMControlHistory gearChangedContHistFour;
    LiteLMControlHistory gearChangedContHistFive;
    LiteLMControlHistory gearChangedContHistSix;
    {
        gearChangedContHistOne = createLiteLMControlHistory("01/04/2010 14:30:00", "01/04/2010 16:30:00", ActiveRestoreEnum.NEW_CONTROL);
        gearChangedContHistTwo = createLiteLMControlHistory("01/04/2010 14:30:00", "01/04/2010 15:00:00", ActiveRestoreEnum.CONTINUE_CONTROL);
        gearChangedContHistThree = createLiteLMControlHistory("01/04/2010 14:30:00", "01/04/2010 15:30:00", ActiveRestoreEnum.OVERRIDE_CONTROL);

        gearChangedContHistFour = createLiteLMControlHistory("01/04/2010 15:30:00", "01/04/2010 16:30:00",ActiveRestoreEnum.NEW_CONTROL);
        gearChangedContHistFive = createLiteLMControlHistory("01/04/2010 15:30:00", "01/04/2010 16:00:00", ActiveRestoreEnum.MANUAL_RESTORE);
        gearChangedContHistSix = createLiteLMControlHistory("01/04/2010 16:00:00", "01/04/2010 16:00:00", ActiveRestoreEnum.MANUAL_RESTORE);
    }
    
    LiteStarsLMControlHistory liteCtrlHistOne;
    {
        liteCtrlHistOne = new LiteStarsLMControlHistory();

        List<LiteBase> controlHistoryList = Lists.newArrayListWithExpectedSize(24);
        liteCtrlHistOne.setLmControlHistory(controlHistoryList);
        
        controlHistoryList.add(overnightContHistStoppedBeforeOvernightControlOne);
        controlHistoryList.add(overnightContHistStoppedBeforeOvernightControlTwo);
        controlHistoryList.add(overnightContHistStoppedBeforeOvernightControlThree);

        controlHistoryList.add(regularContHistWithContuationsOne);
        controlHistoryList.add(regularContHistWithContuationsTwo);
        controlHistoryList.add(regularContHistWithContuationsThree);
        controlHistoryList.add(regularContHistWithContuationsFour);
        controlHistoryList.add(regularContHistWithContuationsFive);
        controlHistoryList.add(regularContHistWithContuationsSix);
        
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayOne);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayTwo);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayThree);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayFour);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayFive);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDaySix);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDaySeven);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayEight);
        controlHistoryList.add(regularContHistWithContuationsThatSpanIntoTheNextDayNine);
        
        controlHistoryList.add(gearChangedContHistOne);
        controlHistoryList.add(gearChangedContHistTwo);
        controlHistoryList.add(gearChangedContHistThree);
        controlHistoryList.add(gearChangedContHistFour);
        controlHistoryList.add(gearChangedContHistFive);
        controlHistoryList.add(gearChangedContHistSix);
    }

    ObservedControlHistory observedControlHistoryOne;
    {
        observedControlHistoryOne = new ObservedControlHistory();
        Instant startDate = dateTimeFormmater.parseDateTime("01/01/2010 00:00:00").toInstant();
        
        // If this breaks, the buildStarsControlHistoryForPeriod general functionality is broken.
        StarsLMControlHistory starsLMControlHistoryOne = 
            service.buildStarsControlHistoryForPeriod(liteCtrlHistOne, startDate, null, timeZone);
        
        observedControlHistoryOne.addAllControlHistoryEntries(starsLMControlHistoryOne.getControlHistoryList());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    // This control duration is for control that has more than one start in a row.
    // This could be caused by the event not being stopped correctly or an error in the servers.
    LiteLMControlHistory doubleStartContHistOne;
    LiteLMControlHistory doubleStartContHistTwo;
    LiteLMControlHistory doubleStartContHistThree;
    LiteLMControlHistory doubleStartContHistFour;
    {
        doubleStartContHistOne = createLiteLMControlHistory("01/04/2010 14:30:00", "01/04/2010 16:30:00", ActiveRestoreEnum.NEW_CONTROL);
        doubleStartContHistTwo = createLiteLMControlHistory("01/04/2010 14:50:00", "01/04/2010 16:30:00",ActiveRestoreEnum.NEW_CONTROL);
        doubleStartContHistThree = createLiteLMControlHistory("01/04/2010 14:50:00", "01/04/2010 16:00:00", ActiveRestoreEnum.MANUAL_RESTORE);
        doubleStartContHistFour = createLiteLMControlHistory("01/04/2010 16:00:00", "01/04/2010 16:00:00", ActiveRestoreEnum.MANUAL_RESTORE);
    }
    
    LiteStarsLMControlHistory doubleStartCtrlHistOne;
    {
        doubleStartCtrlHistOne = new LiteStarsLMControlHistory();

        List<LiteBase> controlHistoryList = Lists.newArrayListWithExpectedSize(4);
        doubleStartCtrlHistOne.setLmControlHistory(controlHistoryList);
        
        controlHistoryList.add(doubleStartContHistOne);
        controlHistoryList.add(doubleStartContHistTwo);
        controlHistoryList.add(doubleStartContHistThree);
        controlHistoryList.add(doubleStartContHistFour);
    }
    
        
    /* Test building control history functionality */
    /**
     * This tests building control history when the start date is in the middle of a 
     * control history event that contains gear changes.
     * 
     * Days                       [------------------------------------------------]
     * Viewable Control History                                   [---------------->
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testStarsControlHistoryForPeriod_One() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/04/2010 14:45:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 2);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  viewableStartDate,
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardMinutes(45));
                                                  
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardMinutes(30));
    }

    /**
     * This tests building control history when the start date is before a 
     * control history event that contains gear changes.
     * 
     * Days                       [------------------------------------------------]
     * Viewable Control History                             [---------------------->
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Two() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/04/2010 12:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 2);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(1));
                                                  
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardMinutes(30));
    }

    
    /**
     * This tests building control history when the start date is in the middle of a 
     * control history event that contains continuations that causes overnight control.
     * 
     * Days                       [------------------------|------------------------]
     * Viewable Control History            [---------------------------------------->
     * Control History Blocks         [----------------------]           [-|-]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Three() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/03/2010 10:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 3);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  viewableStartDate,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(17).plus(Duration.standardMinutes(30)));
                                                  
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(1));

        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThird = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThird,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThird, Duration.standardMinutes(30));

    }

    /**
     * This tests building control history when the start date is before a 
     * control history event that contains continuations that causes overnight control.
     * 
     * Days                       [------------------------|------------------------]
     * Viewable Control History   [------------------------------------------------->
     * Control History Blocks         [----------------------]           [-|-]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Four() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/03/2010 00:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 3);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayOne.getStartDateInstant(),
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(22));
                                                  
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(1));

        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThree = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThree,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThree, Duration.standardMinutes(30));

    }

    /**
     * This tests building control history when the start date is in the middle of a 
     * control history event that contains continuations.
     * 
     * Days                       [------------|------------|------------|------------]
     * Viewable Control History                   [----------------------------------->
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Five() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/02/2010 06:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 4);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  viewableStartDate,
                                                  regularContHistWithContuationsFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(1).plus(Duration.standardMinutes(30)));
        
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayOne.getStartDateInstant(),
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(22));
                                                  
        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThree = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThree,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThree, Duration.standardHours(1));

        // Check Fourth Control History Entry
        ControlHistoryEntry controlHistoryEntryFour = controlHistoryEntryList.get(3);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFour,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFour, Duration.standardMinutes(30));

    }

    /**
     * This tests building control history when the start date matches the beginning of a 
     * control history event that contains continuations.
     * 
     * Days                       [------------|------------|------------|------------]
     * Viewable Control History                  [------------------------------------>
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Six() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/02/2010 05:30:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 4);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  regularContHistWithContuationsTwo.getStartDateInstant(),
                                                  regularContHistWithContuationsFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(2));
        
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayOne.getStartDateInstant(),
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(22));
                                                  
        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThree = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThree,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThree, Duration.standardHours(1));

        // Check Fourth Control History Entry
        ControlHistoryEntry controlHistoryEntryFour = controlHistoryEntryList.get(3);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFour,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFour, Duration.standardMinutes(30));

    }
    
    /**
     * This tests building control history when the start date is before a 
     * control history event that contains continuations.
     * 
     * Days                       [------------|------------|------------|------------]
     * Viewable Control History                [-------------------------------------->
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Seven() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/02/2010 00:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 4);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  regularContHistWithContuationsTwo.getStartDateInstant(),
                                                  regularContHistWithContuationsFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(2));
        
        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayOne.getStartDateInstant(),
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(22));
                                                  
        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThree = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThree,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThree, Duration.standardHours(1));

        // Check Fourth Control History Entry
        ControlHistoryEntry controlHistoryEntryFour = controlHistoryEntryList.get(3);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFour,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFour, Duration.standardMinutes(30));

    }

    /**
     * This tests building control history when the start date is in the middle of a 
     * normal control history event.
     * 
     * Days                       [------------|------------|------------|------------]
     * Viewable Control History               [--------------------------------------->
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Eight() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/01/2010 23:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 5);

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  viewableStartDate,
                                                  overnightContHistStoppedBeforeOvernightControlThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardMinutes(30));

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  regularContHistWithContuationsTwo.getStartDateInstant(),
                                                  regularContHistWithContuationsFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(2));
        
        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThree = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThree,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayOne.getStartDateInstant(),
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThree, Duration.standardHours(22));
                                                  
        // Check Fourth Control History Entry
        ControlHistoryEntry controlHistoryEntryFour = controlHistoryEntryList.get(3);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFour,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFour, Duration.standardHours(1));

        // Check Fifth Control History Entry
        ControlHistoryEntry controlHistoryEntryFive = controlHistoryEntryList.get(4);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFive,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFive, Duration.standardMinutes(30));

    }

    /**
     * This tests building control history when the start date is before a normal control history event.
     * 
     * Days                       [------------|------------|------------|------------]
     * Viewable Control History   [--------------------------------------------------->
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Nine() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = liteCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/01/2010 00:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 5);

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  overnightContHistStoppedBeforeOvernightControlOne.getStartDateInstant(),
                                                  overnightContHistStoppedBeforeOvernightControlThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardHours(1));

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  regularContHistWithContuationsTwo.getStartDateInstant(),
                                                  regularContHistWithContuationsFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardHours(2));
        
        // Check Third Control History Entry
        ControlHistoryEntry controlHistoryEntryThree = controlHistoryEntryList.get(2);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryThree,
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayOne.getStartDateInstant(),
                                                  regularContHistWithContuationsThatSpanIntoTheNextDayEight.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryThree, Duration.standardHours(22));
                                                  
        // Check Fourth Control History Entry
        ControlHistoryEntry controlHistoryEntryFour = controlHistoryEntryList.get(3);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFour,
                                                  gearChangedContHistOne.getStartDateInstant(),
                                                  gearChangedContHistThree.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFour, Duration.standardHours(1));

        // Check Fifth Control History Entry
        ControlHistoryEntry controlHistoryEntryFive = controlHistoryEntryList.get(4);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryFive,
                                                  gearChangedContHistFour.getStartDateInstant(),
                                                  gearChangedContHistFive.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryFive, Duration.standardMinutes(30));

    }

    /**
     * This tests building control history when there are more than one start event in a row.
     * This could be caused by the event not being stopped correctly or an error in the servers.
     * 
     * Days                       [---------------------------------------------------]
     * Viewable Control History   [--------------------------------------------------->
     * Control History Blocks                                  [|--]
     */
    @Test
    public void testStarsControlHistoryForPeriod_Ten() {
        LiteStarsLMControlHistory liteStarsLMControlHistory = doubleStartCtrlHistOne;
        Instant viewableStartDate = dateTimeFormmater.parseDateTime("01/04/2010 00:00:00").toInstant();
        
        StarsLMControlHistory starsLmControlHistory = 
            service.buildStarsControlHistoryForPeriod(liteStarsLMControlHistory, viewableStartDate, null, timeZone);
        
        List<ControlHistoryEntry> controlHistoryEntryList = starsLmControlHistory.getControlHistoryList();
        
        checkNumberOfControlHistoryEntries(controlHistoryEntryList, 2);

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryOne = controlHistoryEntryList.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryOne,
                                                  doubleStartContHistOne.getStartDateInstant(),
                                                  doubleStartContHistTwo.getStartDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryOne, Duration.standardMinutes(20));

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryEntryTwo = controlHistoryEntryList.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryEntryTwo,
                                                  doubleStartContHistTwo.getStartDateInstant(),
                                                  doubleStartContHistFour.getStopDateInstant());
        checkControlHistoryEntryDuration(controlHistoryEntryTwo, Duration.standardMinutes(70));
        
    }
    
    /* Test Control Summary Functionality */
    /**
     * Days                       [------------------------------------------------]
     * Summary Area                                                  [------------->
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testControlSummaryMethod_One(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/04/2010 15:35:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardMinutes(25));
    }

    /**
     * Days                       [------------------------------------------------]
     * Summary Area                                                 [------------->
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testControlSummaryMethod_Two(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/04/2010 15:30:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardMinutes(30));
    }

    /**
     * Days                       [------------------------------------------------]
     * Summary Area                                              [----------------->
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testControlSummaryMethod_Three(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/04/2010 14:45:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardHours(1).plus(Duration.standardMinutes(15)));
    }

    /**
     * Days                       [------------------------------------------------]
     * Summary Area                                           [----------------->
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testControlSummaryMethod_Four(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/04/2010 14:00:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardHours(1).plus(Duration.standardMinutes(30)));
    }

    /**
     * Days                       [------------------------------------------------]
     * Summary Area               [------------------------------------------------>
     * Control History Blocks    <---]                          [---|---]
     */
    @Test
    public void testControlSummaryMethod_Five(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/04/2010 00:00:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardHours(5));
    }

    /**
     * Days                       [------------------------|------------------------]
     * Summary Area               [------------------------------------------------->
     * Control History Blocks         [----------------------]           [-|-]
     */
    @Test
    public void testControlSummaryMethod_Six(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/03/2010 00:00:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardHours(23).plus(Duration.standardMinutes(30)));
    }

    /**
     * Days                       [------------|------------|------------|------------]
     * Summary Area                            [-------------------------------------->
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testControlSummaryMethod_Seven(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/02/2010 00:00:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardHours(25).plus(Duration.standardMinutes(30)));
    }

    /**
     * Days                       [------------|------------|------------|------------]
     * Summary Area               [--------------------------------------------------->
     * Control History Blocks                []  [--]          [------------]     [|]
     */
    @Test
    public void testControlSummaryMethod_Eight(){
        
        ObservedControlHistory observedControlHistory = observedControlHistoryOne;
        Instant summaryStartDate = dateTimeFormmater.parseDateTime("01/01/2010 00:00:00").toInstant();
        
        Duration summaryDuration = 
            LmControlHistoryUtilServiceImpl.calculateSummaryControlValueForPeriod(observedControlHistory.getControlHistoryList(), summaryStartDate);
        
        Assert.assertEquals(summaryDuration, Duration.standardHours(26).plus(Duration.standardMinutes(30)));
    }

    
    /* Current Enrollment Tests */
    /**
     * Enrollment is active and a control event was started after the enrollment began.
     * 
     * Enrollment               [----------------->
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_One() {
        
        ControlHistoryEntry controlHistory = controlHistoryOne;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentOne);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry,
                                                  controlHistoryOne.getStartInstant(),
                                                  controlHistoryOne.getEndInstant());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(), 
                            Duration.standardHours(4));
    }
    

    /**
     * Enrollment started before the control event was started and 
     * was enrolled during the whole duration of the control event before being unenrolled.
     * 
     * Enrollment               [---------------]
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Two() {

        ControlHistoryEntry controlHistory = controlHistoryOne;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry,
                                                  controlHistoryOne.getStartInstant(),
                                                  controlHistoryOne.getEndInstant());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(), 
                            Duration.standardHours(4));
    }

    /**
     * Enrollment started before the control event was started, 
     * but was unenrolled before the control event was over.
     * 
     * Enrollment               [--------------]
     * Control History Event        [----------------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Three() {
        ControlHistoryEntry controlHistory = controlHistoryTwo;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry,
                                                  controlHistoryTwo.getStartInstant(),
                                                  enrollmentTwo.getGroupEnrollStop());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(), 
                            Duration.standardHours(2));
    }
    
    /**
     * The enrollment started after the control history event had already begun.
     * The control event then ended, but the user is still actively enrolled.
     * 
     * Enrollment                     [---------------->
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Four() {
        
        ControlHistoryEntry controlHistory = controlHistoryThree;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentOne);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry,
                                                  enrollmentOne.getGroupEnrollStart(),
                                                  controlHistoryThree.getEndInstant());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(),
                            Duration.standardHours(3));

    }
    
    /**
     * The enrollment started after the control history event had already begun.
     * Then the control event ended, before the user unenrolled from the program.
     * 
     * Enrollment                     [----------------]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Five() {
        
        ControlHistoryEntry controlHistory = controlHistoryThree;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry,
                                                  enrollmentTwo.getGroupEnrollStart(),
                                                  controlHistoryThree.getEndInstant());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(),
                            Duration.standardHours(3));

    }

    /**
     * The enrollment started after the control history event had already begun and the
     * user unenrolled before the control event was finished.
     * 
     * Enrollment                     [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Six() {

        ControlHistoryEntry controlHistory = controlHistoryFour;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry,
                                                  enrollmentTwo.getGroupEnrollStart(),
                                                  enrollmentTwo.getGroupEnrollStop());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(),
                            Duration.standardHours(5));
    }
    
    /**
     * The enrollment started after the control history event had already stopped.
     * 
     * Enrollment                                    [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Seven() {

        ControlHistoryEntry controlHistory = controlHistoryFive;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForZeroControlHistoryEntries(controlHistoryResult);
    }

    /**
     * The user unenrolled from the program before the control history event occurred.
     * 
     * Enrollment               [----]
     * Control History Event           [----------------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriod_Eight() {

        ControlHistoryEntry controlHistory = controlHistorySix;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        checkForZeroControlHistoryEntries(controlHistoryResult);
    }

    // Overnight control tests
    /* Current Enrollment Tests */
    /**
     * Enrollment is active and a control event was started after the enrollment began.
     * 
     * Day               [------------|------------|------------]
     * Enrollment               [----------------->
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateEnrollmentControlPeriodOvernight_One() {
        
        ControlHistoryEntry controlHistory = controlHistoryOvernightOne;
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentThree);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateEnrollmentControlPeriod(controlHistory, enrollments);

        
        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);

        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry, 
                                                  controlHistoryOvernightOne.getStartInstant(), 
                                                  controlHistoryOvernightOne.getEndInstant());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(),
                            Duration.standardHours(5));

    }
    
    /////////////////////// Control History Opt Out Tests //////////////////////
    /**
     * The opt out was started before the control event and is still active, while
     * the control event has already started and finished.
     * 
     * Opt Out                  [----------------->
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_One() {

        ControlHistoryEntry controlHistory = controlHistoryOne;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutOne);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);

        checkForZeroControlHistoryEntries(controlHistoryResult);
    }

    /**
     * The opt out started before the control event was started and 
     * the opt out finished after the control event finished.
     * 
     * Opt Out                   [---------------]
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Two() {

        ControlHistoryEntry controlHistory = controlHistoryOne;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);

        checkForZeroControlHistoryEntries(controlHistoryResult);
    }
    
    /**
     * The opt out started before the control event started, 
     * but the opt out ended before the control event ended.
     * 
     * Opt Out                  [--------------]
     * Control History Event        [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Three() {

        ControlHistoryEntry controlHistory = controlHistoryTwo;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry, 
                                                  optOutTwo.getOptOutStop(), 
                                                  controlHistoryTwo.getEndInstant());
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(),
                            Duration.standardHours(1));

    }
    
    /**
     * The enrollment started after the control history event had already begun.
     * The control event then ended, but the user is still actively enrolled.
     * 
     * Opt Out                        [---------------->
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Four() {
        
        ControlHistoryEntry controlHistory = controlHistoryThree;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutOne);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry, 
                                                  controlHistoryThree.getStartInstant(), 
                                                  optOutOne.getOptOutStart());
        checkControlHistoryEntryDuration(controlHistoryResultEntry, Duration.standardHours(1));

    }
    
    /**
     * The enrollment started after the control history event had already begun.
     * Then the control event ended, before the user unenrolled from the program.
     * 
     * Opt Out                        [----------------]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Five() {
        
        ControlHistoryEntry controlHistory = controlHistoryThree;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry, 
                                                  controlHistoryThree.getStartInstant(), 
                                                  optOutTwo.getOptOutStart());
        checkControlHistoryEntryDuration(controlHistoryResultEntry, Duration.standardHours(1));

    }
    
    /**
     * The enrollment started after the control history event had already begun and the
     * user unenrolled before the control event was finished.
     * 
     * Opt Out                        [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Six() {

        ControlHistoryEntry controlHistory = controlHistoryFour;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);
        
        checkNumberOfControlHistoryEntries(controlHistoryResult, 2);

        // Check First Control History Entry
        ControlHistoryEntry controlHistoryResultEntryOne = controlHistoryResult.get(0);
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntryOne, 
                                                  controlHistoryFour.getStartInstant(), 
                                                  optOutTwo.getOptOutStart());
        checkControlHistoryEntryDuration(controlHistoryResultEntryOne, Duration.standardHours(2).plus(Duration.standardMinutes(30)));

        // Check Second Control History Entry
        ControlHistoryEntry controlHistoryResultEntryTwo = controlHistoryResult.get(1);
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntryTwo, 
                                                  optOutTwo.getOptOutStop(),
                                                  controlHistoryFour.getEndInstant());
        checkControlHistoryEntryDuration(controlHistoryResultEntryTwo, Duration.standardHours(3).plus(Duration.standardMinutes(30)));

    }
    
    /**
     * The enrollment started after the control history event had already stoped.
     * 
     * Opt Out                                       [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Seven() {

        ControlHistoryEntry controlHistory = controlHistoryFive;
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, OptOuts);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry, 
                                                  controlHistoryFive.getStartInstant(), 
                                                  controlHistoryFive.getEndInstant());
        checkControlHistoryEntryDuration(controlHistoryResultEntry, Duration.standardMinutes(30));

    }

    /**
     * The user unenrolled from the program before the control history event occurred.
     * 
     * Opt Out                  [----]
     * Control History Event           [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Eight() {

        ControlHistoryEntry controlHistory = controlHistorySix;
        
        List<LMHardwareControlGroup> optOuts = Lists.newArrayList();
        optOuts.add(optOutTwo);
        
        List<ControlHistoryEntry> controlHistoryResult = 
            LmControlHistoryUtilServiceImpl.calculateOptOutControlHistory(controlHistory, optOuts);

        checkForOneControlHistoryEntries(controlHistoryResult);
        ControlHistoryEntry controlHistoryResultEntry = controlHistoryResult.get(0);
        
        checkControlHistoryEntryStartAndStopDates(controlHistoryResultEntry, 
                                                  controlHistorySix.getStartInstant(), 
                                                  controlHistorySix.getEndInstant());
        checkControlHistoryEntryDuration(controlHistoryResultEntry, Duration.standardMinutes(45));

    }

    
    // Validation Helper Methods
    private void checkForZeroControlHistoryEntries(List<ControlHistoryEntry> controlHistoryEntryList) {
        if (controlHistoryEntryList.size() != 0) {
            Assert.fail("There should be no control history entries in the control history entry list");
        }
    }

    private void checkForOneControlHistoryEntries(List<ControlHistoryEntry> controlHistoryEntryList) {
        if (controlHistoryEntryList.size() != 1) {
            Assert.fail("There should only be one control history entru in the control history entry list");
        }
    }
    
    private void checkNumberOfControlHistoryEntries(List<ControlHistoryEntry> controlHistoryEntryList, int controlHistorySize) {
        if (controlHistoryEntryList.size() != controlHistorySize) {
            Assert.fail("There should only be "+controlHistorySize+" control history entries in the control history entry list");
        }
    }
    
    private void checkControlHistoryEntryStartAndStopDates(ControlHistoryEntry controlHistoryResultEntry,
                                                           ReadableInstant startDate, ReadableInstant stopDate){
        Assert.assertEquals(controlHistoryResultEntry.getStartInstant(), startDate);
        Assert.assertEquals(controlHistoryResultEntry.getEndInstant(), stopDate);
    }
    
    private void checkControlHistoryEntryDuration(ControlHistoryEntry controlHistoryResultEntry, Duration duration) {
        Assert.assertEquals(controlHistoryResultEntry.getControlDuration(), duration);
    }

    // Factory Helper Methods 
    private ControlHistoryEntry createControlHistoryEntry(String startDate, Duration controlDuration, boolean isConstrolling){
        ControlHistoryEntry controlHistoryEntry = new ControlHistoryEntry();

        controlHistoryEntry.setStartInstant(dateTimeFormmater.parseDateTime(startDate).toInstant());
        controlHistoryEntry.setControlDuration(controlDuration);
        controlHistoryEntry.setCurrentlyControlling(isConstrolling);

        return controlHistoryEntry;
    }
    
    private LiteLMControlHistory createLiteLMControlHistory(String startDate, String stopDate, ActiveRestoreEnum activeRestore){
        LiteLMControlHistory liteLMControlHistory = new LiteLMControlHistory();
        
        liteLMControlHistory.setStartDateTime(dateTimeFormmater.parseDateTime(startDate).toInstant().getMillis());
        liteLMControlHistory.setStopDateTime(dateTimeFormmater.parseDateTime(stopDate).toInstant().getMillis());
        liteLMControlHistory.setActiveRestore(activeRestore.getDatabaseRepresentation().toString());
        
        return liteLMControlHistory;
    }
    
}
