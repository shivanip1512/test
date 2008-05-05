package com.cannontech.stars.dr.controlhistory.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;

public class ControlHistoryServiceImplTest {
    private static final DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private final ControlHistoryService service = new ControlHistoryServiceImpl();

    /**
     * Multiple Events with the same control time window.
     * 
     *      / D1 [-----] Program 1/Device 1/Window 1 02:00 - 02:15
     * P1 - | D2 [-----] Program 1/Device 2/Window 2 02:00 - 02:15
     *      \ D3 [-----] Program 1/Device 2/Window 3 02:00 - 02:15
     */
    @Test
    public void test_same_ControlHistoryEvents() throws Exception {
        final int controlHistorySize = 3;
        final int controlHistroyEventSize = 1;
        
        int totalDurationExpected = 900; // 15 minutes
        Date startDate = formatter.parse("02:00:00");
        Date endDate = formatter.parse("02:15:00");
        
        List<ControlHistory> controlHistoryList = new ArrayList<ControlHistory>(controlHistorySize);
        for (int x = 0; x < controlHistorySize; x++) {

            List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>(controlHistroyEventSize);
            for (int i = 0; i < controlHistroyEventSize; i++) {
                ControlHistoryEvent event = new ControlHistoryEvent();
                event.setDuration(totalDurationExpected);
                event.setStartDate(startDate);
                event.setEndDate(endDate);
                eventList.add(event);
            }

            ControlHistory controlHistory = new ControlHistory();
            controlHistory.setCurrentHistory(eventList);
            controlHistoryList.add(controlHistory);
        }

        int totalDurationResult = service.calculateTotalDuration(controlHistoryList);
        Assert.assertEquals("Total Duration was not 15 minutes", totalDurationResult, totalDurationExpected);
    }
    
    /**
     * Multiple Events with time overlap
     * 
     *      / D1 [-----]    Program 1/Device 1/Window 1 02:00 - 04:00
     * P1 - | D2    [-----] Program 1/Device 2/Window 2 03:00 - 06:00
     *      \ D3  [----]    Program 1/Device 3/Window 3 02:15 - 04:00
     */
    @Test
    public void test_time_overlap_ControlHistoryEvents() throws Exception {
        List<ControlHistory> controlHistoryList = new ArrayList<ControlHistory>(3);
        
        ControlHistoryEvent eventForDevice1 = new ControlHistoryEvent();
        eventForDevice1.setDuration(7200); // 2 hours
        eventForDevice1.setStartDate(formatter.parse("02:00:00"));
        eventForDevice1.setEndDate(formatter.parse("04:00:00"));
        
        ControlHistoryEvent eventForDevice2 = new ControlHistoryEvent();
        eventForDevice2.setDuration(7200); // 2 hours
        eventForDevice2.setStartDate(formatter.parse("03:00:00"));
        eventForDevice2.setEndDate(formatter.parse("06:00:00"));
        
        ControlHistoryEvent eventForDevice3 = new ControlHistoryEvent();
        eventForDevice3.setDuration(6300); // 1 hour, 45 minutes
        eventForDevice3.setStartDate(formatter.parse("02:15:00"));
        eventForDevice3.setEndDate(formatter.parse("04:00:00"));
        
        ControlHistory controlHistoryForDevice1 = new ControlHistory();
        controlHistoryForDevice1.setCurrentHistory(Arrays.asList(eventForDevice1));
        
        ControlHistory controlHistoryForDevice2 = new ControlHistory();
        controlHistoryForDevice2.setCurrentHistory(Arrays.asList(eventForDevice2));

        ControlHistory controlHistoryForDevice3 = new ControlHistory();
        controlHistoryForDevice3.setCurrentHistory(Arrays.asList(eventForDevice3));

        controlHistoryList.add(controlHistoryForDevice1);
        controlHistoryList.add(controlHistoryForDevice2);
        controlHistoryList.add(controlHistoryForDevice3);
        
        int totalDurationExpected = 14400; // 4 hours
        int totalDurationResult = service.calculateTotalDuration(controlHistoryList);
        Assert.assertEquals("Total Duration was not 4 hours", totalDurationResult, totalDurationExpected);
    }
    
    /**
     * Multiple Events with time gap, overlap, and multiple same events
     * 
     *      / D1 [--]  [--]  Program 1/Device 1/Window 1 02:00 - 04:00, Window 2 06:00 - 07:00
     * P1 - | D2        [--] Program 1/Device 2/Window 3 06:30 - 07:30
     *      \ D3 [---]       Program 1/Device 3/Window 4 02:00 - 04:15
     *      \ D4 [--]        Program 1/Device 4/Window 5 02:00 - 04:00
     */
    @Test
    public void test_time_gap_ControlHistoryEvents() throws Exception {
        List<ControlHistory> controlHistoryList = new ArrayList<ControlHistory>(4);
        
        ControlHistoryEvent eventForDevice1Window1 = new ControlHistoryEvent();
        eventForDevice1Window1.setDuration(7200); // 2 hours
        eventForDevice1Window1.setStartDate(formatter.parse("02:00:00"));
        eventForDevice1Window1.setEndDate(formatter.parse("04:00:00"));
        
        ControlHistoryEvent eventForDevice1Window2 = new ControlHistoryEvent();
        eventForDevice1Window2.setDuration(3600); // 1 hour
        eventForDevice1Window2.setStartDate(formatter.parse("06:00:00"));
        eventForDevice1Window2.setEndDate(formatter.parse("07:00:00"));
        
        ControlHistoryEvent eventForDevice2 = new ControlHistoryEvent();
        eventForDevice2.setDuration(3600); // 1 hour
        eventForDevice2.setStartDate(formatter.parse("06:30:00"));
        eventForDevice2.setEndDate(formatter.parse("07:30:00"));
        
        ControlHistoryEvent eventForDevice3 = new ControlHistoryEvent();
        eventForDevice3.setDuration(8100); // 2 hours, 15 minutes
        eventForDevice3.setStartDate(formatter.parse("02:00:00"));
        eventForDevice3.setEndDate(formatter.parse("04:15:00"));
        
        ControlHistoryEvent eventForDevice4 = new ControlHistoryEvent();
        eventForDevice4.setDuration(7200); // 2 hours
        eventForDevice4.setStartDate(formatter.parse("02:00:00"));
        eventForDevice4.setEndDate(formatter.parse("04:00:00"));

        List<ControlHistoryEvent> device1EventList = Arrays.asList(eventForDevice1Window1, eventForDevice1Window2);
        ControlHistory controlHistoryForDevice1 = new ControlHistory();
        controlHistoryForDevice1.setCurrentHistory(device1EventList);
        
        ControlHistory controlHistoryForDevice2 = new ControlHistory();
        controlHistoryForDevice2.setCurrentHistory(Arrays.asList(eventForDevice2));
        
        ControlHistory controlHistoryForDevice3 = new ControlHistory();
        controlHistoryForDevice3.setCurrentHistory(Arrays.asList(eventForDevice3));
        
        ControlHistory controlHistoryForDevice4 = new ControlHistory();
        controlHistoryForDevice4.setCurrentHistory(Arrays.asList(eventForDevice4));

        controlHistoryList.add(controlHistoryForDevice1);
        controlHistoryList.add(controlHistoryForDevice2);
        controlHistoryList.add(controlHistoryForDevice3);
        controlHistoryList.add(controlHistoryForDevice4);
        
        int totalDurationExpected = 13500; // 3 hours, 45 minutes
        int totalDurationResult = service.calculateTotalDuration(controlHistoryList);
        Assert.assertEquals("Total Duration was not 3 hours, 45 minutes", totalDurationResult, totalDurationExpected);
    }
    
}
