package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DayOfWeekTest {

    @Test
    public void Test_buildDBPersistent_YYYYYYY() {
        List<DayOfWeek> daySelections = new ArrayList<>();
        daySelections.add(DayOfWeek.SUNDAY);
        daySelections.add(DayOfWeek.MONDAY);
        daySelections.add(DayOfWeek.TUESDAY);
        daySelections.add(DayOfWeek.WEDNESDAY);
        daySelections.add(DayOfWeek.THURSDAY);
        daySelections.add(DayOfWeek.FRIDAY);
        daySelections.add(DayOfWeek.SATURDAY);
        String daySelection = DayOfWeek.buildDBPersistent(daySelections);
        Assert.assertTrue("Day Selection length mismatch", daySelection.length() == 7);
        Assert.assertTrue("Day Selection Character mismatch", !daySelection.contains("N"));
    }

    @Test
    public void Test_buildDBPersistent_NNNNNNN() {
        List<DayOfWeek> daySelections = new ArrayList<>();
        String daySelection = DayOfWeek.buildDBPersistent(daySelections);
        Assert.assertTrue("Day Selection length mismatch", daySelection.length() == 7);
        Assert.assertTrue("Day Selection Character mismatch", !daySelection.contains("Y"));
        daySelection = DayOfWeek.buildDBPersistent(null);
        Assert.assertTrue("Day Selection length mismatch", daySelection.length() == 7);
        Assert.assertTrue("Day Selection Character mismatch", !daySelection.contains("Y"));
    }
    
    @Test
    public void Test_buildDBPersistent_YNNYNNY() {
        List<DayOfWeek> daySelections = new ArrayList<>();
        daySelections.add(DayOfWeek.SUNDAY);
        daySelections.add(DayOfWeek.WEDNESDAY);
        daySelections.add(DayOfWeek.SATURDAY);
        String daySelection = DayOfWeek.buildDBPersistent(daySelections);
        Assert.assertTrue("Day Selection length mismatch", daySelection.length() == 7);
        boolean flag = daySelection.charAt(0) == 'Y' && daySelection.charAt(3) == 'Y' && daySelection.charAt(6) == 'Y';
        Assert.assertTrue("Day Selection Character mismatch", flag);
    }
    
    @Test
    public void Test_buildModelRepresentation_YYYYYYY() {
        List<DayOfWeek> daySelections= DayOfWeek.buildModelRepresentation("YYYYYYY");
        Assert.assertTrue("Day Selection length mismatch", daySelections.size() == 7);
    }

    @Test
    public void Test_buildModelRepresentation_NNNNNNN() {
        List<DayOfWeek> daySelections = DayOfWeek.buildModelRepresentation("NNNNNNN");
        Assert.assertTrue("Day Selection length mismatch", daySelections.size() == 0);
    }

    @Test
    public void Test_buildModelRepresentation_YNNYNNY() {
        List<DayOfWeek> daySelections = DayOfWeek.buildModelRepresentation("YNNYNNY");
        Assert.assertTrue("Day Selection length mismatch", daySelections.size() == 3);
        Assert.assertTrue("Day Selection mismatch", daySelections.get(0) == DayOfWeek.SUNDAY);
        Assert.assertTrue("Day Selection mismatch", daySelections.get(1) == DayOfWeek.WEDNESDAY);
        Assert.assertTrue("Day Selection mismatch", daySelections.get(2) == DayOfWeek.SATURDAY);
        
    }
}
