package com.cannontech.stars.dr.controlHistory.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Minutes;
import org.joda.time.ReadableDuration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlHistory.service.StarsControlHistoryService;
import com.cannontech.stars.dr.controlHistory.service.impl.StarsControlHistoryServiceImpl;

public class StarsControlHistoryServiceImplTest {
    private static final DateTimeFormatter dateTimeFormmater = 
        DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(DateTimeZone.forOffsetHoursMinutes(5, 0));
    private final StarsControlHistoryService service = new StarsControlHistoryServiceImpl();

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
        
        Duration totalDurationExpected = Minutes.minutes(15).toStandardDuration();
        Instant startDate = dateTimeFormmater.parseDateTime("01/01/2010 02:00:00").toInstant();
        Instant endDate = dateTimeFormmater.parseDateTime("01/01/2010 02:15:00").toInstant();
        
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

        ReadableDuration totalDurationResult = service.calculateTotalDuration(controlHistoryList);
        assertEquals(totalDurationResult, totalDurationExpected, "Total Duration was not 15 minutes");
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
        eventForDevice1.setDuration(new Duration(7200000)); // 2 hours
        eventForDevice1.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 02:00:00").toInstant());
        eventForDevice1.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 04:00:00").toInstant());
        
        ControlHistoryEvent eventForDevice2 = new ControlHistoryEvent();
        eventForDevice2.setDuration(new Duration(7200000)); // 2 hours
        eventForDevice2.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 03:00:00").toInstant());
        eventForDevice2.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 06:00:00").toInstant());
        
        ControlHistoryEvent eventForDevice3 = new ControlHistoryEvent();
        eventForDevice3.setDuration(new Duration(6300000)); // 1 hour, 45 minutes
        eventForDevice3.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 02:15:00").toInstant());
        eventForDevice3.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 04:00:00").toInstant());
        
        ControlHistory controlHistoryForDevice1 = new ControlHistory();
        controlHistoryForDevice1.setCurrentHistory(Arrays.asList(eventForDevice1));
        
        ControlHistory controlHistoryForDevice2 = new ControlHistory();
        controlHistoryForDevice2.setCurrentHistory(Arrays.asList(eventForDevice2));

        ControlHistory controlHistoryForDevice3 = new ControlHistory();
        controlHistoryForDevice3.setCurrentHistory(Arrays.asList(eventForDevice3));

        controlHistoryList.add(controlHistoryForDevice1);
        controlHistoryList.add(controlHistoryForDevice2);
        controlHistoryList.add(controlHistoryForDevice3);
        
        ReadableDuration totalDurationExpected = new Duration(14400000); // 4 hours
        ReadableDuration totalDurationResult = service.calculateTotalDuration(controlHistoryList);
        assertEquals(totalDurationResult, totalDurationExpected, "Total Duration was not 4 hours");
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
        eventForDevice1Window1.setDuration(new Duration(7200000)); // 2 hours
        eventForDevice1Window1.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 02:00:00").toInstant());
        eventForDevice1Window1.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 04:00:00").toInstant());
        
        ControlHistoryEvent eventForDevice1Window2 = new ControlHistoryEvent();
        eventForDevice1Window2.setDuration(new Duration(3600000)); // 1 hour
        eventForDevice1Window2.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 06:00:00").toInstant());
        eventForDevice1Window2.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 07:00:00").toInstant());
        
        ControlHistoryEvent eventForDevice2 = new ControlHistoryEvent();
        eventForDevice2.setDuration(new Duration(3600000)); // 1 hour
        eventForDevice2.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 06:30:00").toInstant());
        eventForDevice2.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 07:30:00").toInstant());
        
        ControlHistoryEvent eventForDevice3 = new ControlHistoryEvent();
        eventForDevice3.setDuration(new Duration(8100000)); // 2 hours, 15 minutes
        eventForDevice3.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 02:00:00").toInstant());
        eventForDevice3.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 04:15:00").toInstant());
        
        ControlHistoryEvent eventForDevice4 = new ControlHistoryEvent();
        eventForDevice4.setDuration(new Duration(7200000)); // 2 hours
        eventForDevice4.setStartDate(dateTimeFormmater.parseDateTime("01/01/2010 02:00:00").toInstant());
        eventForDevice4.setEndDate(dateTimeFormmater.parseDateTime("01/01/2010 04:00:00").toInstant());

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
        
        ReadableDuration totalDurationExpected = new Duration(13500000); // 3 hours, 45 minutes
        ReadableDuration totalDurationResult = service.calculateTotalDuration(controlHistoryList);
        assertEquals(totalDurationResult, totalDurationExpected, "Total Duration was not 3 hours, 45 minutes");
    }
    
}
