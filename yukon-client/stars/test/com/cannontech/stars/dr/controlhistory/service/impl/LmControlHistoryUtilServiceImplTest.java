package com.cannontech.stars.dr.controlhistory.service.impl;

import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.cannontech.stars.dr.controlhistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.google.common.collect.Lists;

public class LmControlHistoryUtilServiceImplTest {
    private static final DateTimeFormatter dateTimeFormmater = 
        DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(DateTimeZone.forOffsetHoursMinutes(5, 0));
    private final LmControlHistoryUtilService service = new LmControlHistoryUtilServiceImpl();

    // Control History Entries
    ControlHistory controlHistoryOne;
    ControlHistory controlHistoryTwo;
    ControlHistory controlHistoryThree;
    ControlHistory controlHistoryFour;
    ControlHistory controlHistoryFive;
    ControlHistory controlHistorySix;
    {
        controlHistoryOne = new ControlHistory();
        controlHistoryOne.setStartInstant(dateTimeFormmater.parseDateTime("01/01/2010 04:00:00").toInstant());
        controlHistoryOne.setControlDuration(Duration.standardHours(4));
        
        controlHistoryTwo = new ControlHistory();
        controlHistoryTwo.setStartInstant(dateTimeFormmater.parseDateTime("01/01/2010 06:30:00").toInstant());
        controlHistoryTwo.setControlDuration(Duration.standardHours(3));
    
        controlHistoryThree = new ControlHistory();
        controlHistoryThree.setStartInstant(dateTimeFormmater.parseDateTime("01/01/2010 02:30:00").toInstant());
        controlHistoryThree.setControlDuration(Duration.standardHours(4));
    
        controlHistoryFour = new ControlHistory();
        controlHistoryFour.setStartInstant(dateTimeFormmater.parseDateTime("01/01/2010 01:00:00").toInstant());
        controlHistoryFour.setControlDuration(Duration.standardHours(11));
    
        controlHistoryFive = new ControlHistory();
        controlHistoryFive.setStartInstant(dateTimeFormmater.parseDateTime("01/01/2010 10:00:00").toInstant());
        controlHistoryFive.setControlDuration(Duration.standardMinutes(30));
    
        controlHistorySix = new ControlHistory();
        controlHistorySix.setStartInstant(dateTimeFormmater.parseDateTime("01/01/2010 01:00:00").toInstant());
        controlHistorySix.setControlDuration(Duration.standardMinutes(45));
    }
    
    // Enrollment Entries
    LMHardwareControlGroup enrollmentOne;
    LMHardwareControlGroup enrollmentTwo;
    {
        enrollmentOne = new LMHardwareControlGroup();
        enrollmentOne.setGroupEnrollStart(dateTimeFormmater.parseDateTime("01/01/2010 03:30:00").toInstant());
        
        enrollmentTwo = new LMHardwareControlGroup();
        enrollmentTwo.setGroupEnrollStart(dateTimeFormmater.parseDateTime("01/01/2010 03:30:00").toInstant());
        enrollmentTwo.setGroupEnrollStop(dateTimeFormmater.parseDateTime("01/01/2010 08:30:00").toInstant());
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
    
    /* Current Enrollment Tests */
    /**
     * Enrollment is active and a control event was started after the enrollment began.
     * 
     * Enrollment               [----------------->
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_One() {
        
        ControlHistory controlHistory = controlHistoryOne;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentOne);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryOne.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(4));
    }
    
    /**
     * Enrollment started before the control event was started and 
     * was enrolled during the whole duration of the control event before being unenrolled.
     * 
     * Enrollment               [---------------]
     * Control History Event        [---------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Two() {

        ControlHistory controlHistory = controlHistoryOne;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration currentTestDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryOne.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(currentTestDuration, Duration.standardHours(0));
        
    }

    @Test
    public void testCalculatePreviousEnrollmentControlPeriod_Two() {

        ControlHistory controlHistory = controlHistoryOne;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration previousTestDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryOne.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(previousTestDuration, Duration.standardHours(4));
        
    }

    
    /**
     * Enrollment started before the control event was started, 
     * but was unenrolled before the control event was over.
     * 
     * Enrollment               [--------------]
     * Control History Event        [----------------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Three() {

        ControlHistory controlHistory = controlHistoryTwo;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryTwo.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }

    @Test
    public void testCalculatePreviousEnrollmentControlPeriod_Three() {

        ControlHistory controlHistory = controlHistoryTwo;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryTwo.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(2));
    }
    
    
    /**
     * The enrollment started after the control history event had already begun.
     * The control event then ended, but the user is still actively enrolled.
     * 
     * Enrollment                     [---------------->
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Four() {
        
        ControlHistory controlHistory = controlHistoryThree;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentOne);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(enrollmentOne.getGroupEnrollStart())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(3));
    }

    @Test
    public void testCalculatePreviousEnrollmentControlPeriod_Four() {
        
        ControlHistory controlHistory = controlHistoryThree;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentOne);
        
        Duration testDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryThree.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }

    
    /**
     * The enrollment started after the control history event had already begun.
     * Then the control event ended, before the user unenrolled from the program.
     * 
     * Enrollment                     [----------------]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Five() {
        
        ControlHistory controlHistory = controlHistoryThree;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryThree.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }

    @Test
    public void testCalculatePreviousEnrollmentControlPeriod_Five() {
        
        ControlHistory controlHistory = controlHistoryThree;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(enrollmentTwo.getGroupEnrollStart())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(3));
    }
    
    /**
     * The enrollment started after the control history event had already begun and the
     * user unenrolled before the control event was finished.
     * 
     * Enrollment                     [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Six() {

        ControlHistory controlHistory = controlHistoryFour;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryFour.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }
    
    public void testCalculatePreviousEnrollmentControlPeriod_Six() {

        ControlHistory controlHistory = controlHistoryFour;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryFour.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(5));
    }
    
    /**
     * The enrollment started after the control history event had already stoped.
     * 
     * Enrollment                                    [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Seven() {

        ControlHistory controlHistory = controlHistoryFive;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryFive.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }

    @Test
    public void testCalculatePreviousEnrollmentControlPeriod_Seven() {

        ControlHistory controlHistory = controlHistoryFive;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistoryFive.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }

    /**
     * The user unenrolled from the program before the control history event occurred.
     * 
     * Enrollment               [----]
     * Control History Event           [----------------]
     */
    @Test
    public void testCalculateCurrentEnrollmentControlPeriod_Eight() {

        ControlHistory controlHistory = controlHistorySix;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculateCurrentEnrollmentControlPeriod(controlHistory, 
                                                            controlHistory.getControlDuration(),
                                                            stopInstant,
                                                            enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistorySix.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
    }

    @Test
    public void testCalculatePreviousEnrollmentControlPeriod_Eight() {

        ControlHistory controlHistory = controlHistorySix;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(enrollmentTwo);
        
        Duration testDuration = 
            service.calculatePreviousEnrollmentControlPeriod(controlHistory, 
                                                             controlHistory.getControlDuration(),
                                                             stopInstant,
                                                             enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistorySix.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
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

        ControlHistory controlHistory = controlHistoryOne;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutOne);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);

        if (!controlHistory.getStartInstant().equals(controlHistoryOne.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(0));
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

        ControlHistory controlHistory = controlHistoryOne;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        Duration currentTestDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);

        if (!controlHistory.getStartInstant().equals(controlHistoryOne.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(currentTestDuration, Duration.standardHours(0));
        
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

        ControlHistory controlHistory = controlHistoryTwo;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);

        if (!controlHistory.getStartInstant().equals(optOutTwo.getOptOutStop())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(1));
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
        
        ControlHistory controlHistory = controlHistoryThree;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutOne);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);

        if (!controlHistory.getStartInstant().equals(controlHistoryThree.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(1));
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
        
        ControlHistory controlHistory = controlHistoryThree;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);

        if (!controlHistory.getStartInstant().equals(controlHistoryThree.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(1));
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

        ControlHistory controlHistory = controlHistoryFour;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);
        
        if (!controlHistory.getStartInstant().equals(controlHistoryFour.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardHours(6));
    }
    
    /**
     * The enrollment started after the control history event had already stoped.
     * 
     * Opt Out                                       [----]
     * Control History Event    [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Seven() {

        ControlHistory controlHistory = controlHistoryFive;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> OptOuts = Lists.newArrayList();
        OptOuts.add(optOutTwo);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  OptOuts);

        if (!controlHistory.getStartInstant().equals(controlHistoryFive.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardMinutes(30));
    }

    /**
     * The user unenrolled from the program before the control history event occurred.
     * 
     * Opt Out                  [----]
     * Control History Event           [----------------]
     */
    @Test
    public void testCalculateCurrentOptOutControlHistory_Eight() {

        ControlHistory controlHistory = controlHistorySix;
        Instant stopInstant = controlHistory.getStartInstant().plus(controlHistory.getControlDuration());
        
        List<LMHardwareControlGroup> enrollments = Lists.newArrayList();
        enrollments.add(optOutTwo);
        
        Duration testDuration = 
            service.calculateOptOutControlHistory(controlHistory, 
                                                  controlHistory.getControlDuration(),
                                                  stopInstant,
                                                  enrollments);

        if (!controlHistory.getStartInstant().equals(controlHistorySix.getStartInstant())) {
            Assert.fail();
        }
        Assert.assertEquals(testDuration, Duration.standardMinutes(45));
    }
}
