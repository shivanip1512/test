package com.cannontech.services.smartNotification.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IntervalsTest {

    @Test
    public void test_intervals_intervalsAreBlank() {
        Intervals intervals = new Intervals("", "0,1,3,5,15,30");

        String testFailMessage = "Default intervals not used when specified intervals are blank.";
        assertEquals(0, intervals.getFirstInterval(), testFailMessage);
        assertEquals(1, intervals.getNextInterval(0), testFailMessage);
        assertEquals(3, intervals.getNextInterval(1), testFailMessage);
        assertEquals(5, intervals.getNextInterval(3), testFailMessage);
        assertEquals(15, intervals.getNextInterval(5), testFailMessage);
        assertEquals(30, intervals.getNextInterval(15), testFailMessage);
        assertEquals(30, intervals.getNextInterval(30), testFailMessage);
    }
    
    @Test
    public void test_intervals_intervalsAreEmpty() {
        Intervals intervals = new Intervals("     ", "0,1,3,5,15,30");

        String testFailMessage = "Default intervals not used when specified intervals are empty.";
        assertEquals(0, intervals.getFirstInterval(), testFailMessage);
        assertEquals(1, intervals.getNextInterval(0), testFailMessage);
        assertEquals(3, intervals.getNextInterval(1), testFailMessage);
        assertEquals(5, intervals.getNextInterval(3), testFailMessage);
        assertEquals(15, intervals.getNextInterval(5), testFailMessage);
        assertEquals(30, intervals.getNextInterval(15), testFailMessage);
        assertEquals(30, intervals.getNextInterval(30), testFailMessage);
    }
    
    @Test
    public void test_intervals_intervalsAreOutOfOrder() {
        Intervals intervals = new Intervals("5,3,1,4,2", "0,1,3,5,15,30");

        String testFailMessage = "Out of order intervals are not ordered correctly when parsed.";
        assertEquals(1, intervals.getFirstInterval(), testFailMessage);
        assertEquals(2, intervals.getNextInterval(1), testFailMessage);
        assertEquals(3, intervals.getNextInterval(2), testFailMessage);
        assertEquals(4, intervals.getNextInterval(3), testFailMessage);
        assertEquals(5, intervals.getNextInterval(4), testFailMessage);
        assertEquals(5, intervals.getNextInterval(5), testFailMessage);
    }
    
    @Test
    public void test_intervals_duplicateIntervals() {
        Intervals intervals = new Intervals("0,0,1,1,2,3", "0,1,3,5,15,30");
        
        String testFailMessage = "Duplicate intervals are not handled correctly when parsed.";
        assertEquals(0, intervals.getFirstInterval(), testFailMessage);
        assertEquals(1, intervals.getNextInterval(0), testFailMessage);
        assertEquals(2, intervals.getNextInterval(1), testFailMessage);
        assertEquals(3, intervals.getNextInterval(2), testFailMessage);
        assertEquals(3, intervals.getNextInterval(3), testFailMessage);
    }
    
    @Test
    public void test_intervals_negativeIntervals() {
        Intervals intervals = 
                new Intervals("-1,0,1,2,3", "0,1,3,5,15,30");
        
        String testFailMessage = "Default intervals not used when specified intervals include negatives";
        assertEquals(0, intervals.getFirstInterval(), testFailMessage);
        assertEquals(1, intervals.getNextInterval(0), testFailMessage);
        assertEquals(3, intervals.getNextInterval(1), testFailMessage);
        assertEquals(5, intervals.getNextInterval(3), testFailMessage);
        assertEquals(15, intervals.getNextInterval(5), testFailMessage);
        assertEquals(30, intervals.getNextInterval(15), testFailMessage);
        assertEquals(30, intervals.getNextInterval(30), testFailMessage);
    }
    
    @Test
    public void test_intervals_invalidIntervals() {
        Intervals intervals = 
                new Intervals("0,1,2,3,A,%,!", "0,1,3,5,15,30");
        
        String testFailMessage = "Default intervals not used when specified intervals are invalid";
        assertEquals(0, intervals.getFirstInterval(), testFailMessage);
        assertEquals(1, intervals.getNextInterval(0), testFailMessage);
        assertEquals(3, intervals.getNextInterval(1), testFailMessage);
        assertEquals(5, intervals.getNextInterval(3), testFailMessage);
        assertEquals(15, intervals.getNextInterval(5), testFailMessage);
        assertEquals(30, intervals.getNextInterval(15), testFailMessage);
        assertEquals(30, intervals.getNextInterval(30), testFailMessage);
    }
}
