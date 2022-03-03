package com.cannontech.common.dr.setup;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

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
        assertTrue(daySelection.length() == 7, "Day Selection length mismatch");
        assertTrue(!daySelection.contains("N"), "Day Selection Character mismatch");
    }

    @Test
    public void Test_buildDBPersistent_NNNNNNN() {
        List<DayOfWeek> daySelections = new ArrayList<>();
        String daySelection = DayOfWeek.buildDBPersistent(daySelections);
        assertTrue(daySelection.length() == 7, "Day Selection length mismatch");
        assertTrue(!daySelection.contains("Y"), "Day Selection Character mismatch");
        daySelection = DayOfWeek.buildDBPersistent(null);
        assertTrue(daySelection.length() == 7, "Day Selection length mismatch");
        assertTrue(!daySelection.contains("Y"), "Day Selection Character mismatch");
    }
    
    @Test
    public void Test_buildDBPersistent_YNNYNNY() {
        List<DayOfWeek> daySelections = new ArrayList<>();
        daySelections.add(DayOfWeek.SUNDAY);
        daySelections.add(DayOfWeek.WEDNESDAY);
        daySelections.add(DayOfWeek.SATURDAY);
        String daySelection = DayOfWeek.buildDBPersistent(daySelections);
        assertTrue(daySelection.length() == 7, "Day Selection length mismatch");
        boolean flag = daySelection.charAt(0) == 'Y' && daySelection.charAt(3) == 'Y' && daySelection.charAt(6) == 'Y';
        assertTrue(flag, "Day Selection Character mismatch");
    }
    
    @Test
    public void Test_buildModelRepresentation_YYYYYYY() {
        List<DayOfWeek> daySelections= DayOfWeek.buildModelRepresentation("YYYYYYY");
        assertTrue(daySelections.size() == 7, "Day Selection length mismatch");
    }

    @Test
    public void Test_buildModelRepresentation_NNNNNNN() {
        List<DayOfWeek> daySelections = DayOfWeek.buildModelRepresentation("NNNNNNN");
        assertTrue(daySelections.size() == 0, "Day Selection length mismatch");
    }

    @Test
    public void Test_buildModelRepresentation_YNNYNNY() {
        List<DayOfWeek> daySelections = DayOfWeek.buildModelRepresentation("YNNYNNY");
        assertTrue(daySelections.size() == 3, "Day Selection length mismatch");
        assertTrue(daySelections.get(0) == DayOfWeek.SUNDAY, "Day Selection mismatch");
        assertTrue(daySelections.get(1) == DayOfWeek.WEDNESDAY, "Day Selection mismatch");
        assertTrue(daySelections.get(2) == DayOfWeek.SATURDAY, "Day Selection mismatch");
        
    }
}
