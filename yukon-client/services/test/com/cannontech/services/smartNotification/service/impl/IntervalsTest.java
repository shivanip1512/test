package com.cannontech.services.smartNotification.service.impl;

import org.junit.Assert;
import org.junit.Test;

public class IntervalsTest {

    @Test
    public void test_intervals_intervalsAreBlank() {
        Intervals intervals = new Intervals("", "0,1,3,5,15,30");
        
        String testFailMessage = "Default intervals not used when specified intervals are blank.";
        Assert.assertEquals(testFailMessage, 0, intervals.getFirstInterval().intValue());
        Assert.assertEquals(testFailMessage, 1, intervals.getNextInterval(0).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(1).intValue());
        Assert.assertEquals(testFailMessage, 5, intervals.getNextInterval(3).intValue());
        Assert.assertEquals(testFailMessage, 15, intervals.getNextInterval(5).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(15).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(30).intValue());
    }
    
    @Test
    public void test_intervals_intervalsAreEmpty() {
        Intervals intervals = new Intervals("     ", "0,1,3,5,15,30");
        
        String testFailMessage = "Default intervals not used when specified intervals are empty.";
        Assert.assertEquals(testFailMessage, 0, intervals.getFirstInterval().intValue());
        Assert.assertEquals(testFailMessage, 1, intervals.getNextInterval(0).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(1).intValue());
        Assert.assertEquals(testFailMessage, 5, intervals.getNextInterval(3).intValue());
        Assert.assertEquals(testFailMessage, 15, intervals.getNextInterval(5).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(15).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(30).intValue());
    }
    
    @Test
    public void test_intervals_intervalsAreOutOfOrder() {
        Intervals intervals = new Intervals("5,3,1,4,2", "0,1,3,5,15,30");
        
        String testFailMessage = "Out of order intervals are not ordered correctly when parsed.";
        Assert.assertEquals(testFailMessage, 1, intervals.getFirstInterval().intValue());
        Assert.assertEquals(testFailMessage, 2, intervals.getNextInterval(1).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(2).intValue());
        Assert.assertEquals(testFailMessage, 4, intervals.getNextInterval(3).intValue());
        Assert.assertEquals(testFailMessage, 5, intervals.getNextInterval(4).intValue());
        Assert.assertEquals(testFailMessage, 5, intervals.getNextInterval(5).intValue());
    }
    
    @Test
    public void test_intervals_duplicateIntervals() {
        Intervals intervals = new Intervals("0,0,1,1,2,3", "0,1,3,5,15,30");
        
        String testFailMessage = "Duplicate intervals are not handled correctly when parsed.";
        Assert.assertEquals(testFailMessage, 0, intervals.getFirstInterval().intValue());
        Assert.assertEquals(testFailMessage, 1, intervals.getNextInterval(0).intValue());
        Assert.assertEquals(testFailMessage, 2, intervals.getNextInterval(1).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(2).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(3).intValue());
    }
    
    @Test
    public void test_intervals_negativeIntervals() {
        Intervals intervals = 
                new Intervals("-1,0,1,2,3", "0,1,3,5,15,30");
        
        String testFailMessage = "Default intervals not used when specified intervals include negatives";
        Assert.assertEquals(testFailMessage, 0, intervals.getFirstInterval().intValue());
        Assert.assertEquals(testFailMessage, 1, intervals.getNextInterval(0).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(1).intValue());
        Assert.assertEquals(testFailMessage, 5, intervals.getNextInterval(3).intValue());
        Assert.assertEquals(testFailMessage, 15, intervals.getNextInterval(5).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(15).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(30).intValue());
    }
    
    @Test
    public void test_intervals_invalidIntervals() {
        Intervals intervals = 
                new Intervals("0,1,2,3,A,%,!", "0,1,3,5,15,30");
        
        String testFailMessage = "Default intervals not used when specified intervals are invalid";
        Assert.assertEquals(testFailMessage, 0, intervals.getFirstInterval().intValue());
        Assert.assertEquals(testFailMessage, 1, intervals.getNextInterval(0).intValue());
        Assert.assertEquals(testFailMessage, 3, intervals.getNextInterval(1).intValue());
        Assert.assertEquals(testFailMessage, 5, intervals.getNextInterval(3).intValue());
        Assert.assertEquals(testFailMessage, 15, intervals.getNextInterval(5).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(15).intValue());
        Assert.assertEquals(testFailMessage, 30, intervals.getNextInterval(30).intValue());
    }
}
