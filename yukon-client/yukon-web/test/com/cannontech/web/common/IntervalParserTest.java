package com.cannontech.web.common;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.IntervalParser;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.impl.DateFormattingServiceImpl;
import com.cannontech.user.YukonUserContext;

public class IntervalParserTest {

    private DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
    private DateFormattingService dateFormattingService = new DateFormattingServiceImpl() {
        @Override
        public String format(Object object, DateFormatEnum type, YukonUserContext userContext) {
            return ((Instant) object).toString(format);
        }
    };

    private static final Logger log = YukonLogManager.getLogger(IntervalParserTest.class);

    @Test
    public void test_TimeIntervals_MINUTES_5() throws Exception {
        TimeIntervals interval = TimeIntervals.MINUTES_5;
        testIntervalParser(interval, "09/26/2020 00:00:00", "09/26/2020 00:15:00",
                List.of("09/26/2020 00:05:00", "09/26/2020 00:10:00", "09/26/2020 00:15:00"));
        testIntervalParser(interval, "09/26/2020 00:05:00", "09/26/2020 00:14:00", List.of("09/26/2020 00:10:00"));
        testIntervalParser(interval, "09/26/2020 00:05:00", "09/26/2020 00:9:00", List.of());
    }

    @Test
    public void test_TimeIntervals_MINUTES_15() throws Exception {
        TimeIntervals interval = TimeIntervals.MINUTES_15;
        testIntervalParser(interval, "09/26/2020 01:00:00", "09/26/2020 02:15:00",
                List.of("09/26/2020 01:15:00", "09/26/2020 01:30:00", "09/26/2020 01:45:00", "09/26/2020 02:00:00",
                        "09/26/2020 02:15:00"));
        testIntervalParser(interval, "09/26/2020 01:15:00", "09/26/2020 01:30:00", List.of("09/26/2020 01:30:00"));
        testIntervalParser(interval, "09/26/2020 01:15:00", "09/26/2020 01:15:00", List.of());
    }

    @Test
    public void test_TimeIntervals_MINUTES_30() throws Exception {
        TimeIntervals interval = TimeIntervals.MINUTES_30;
        testIntervalParser(interval, "09/26/2020 01:00:00", "09/26/2020 02:15:00",
                List.of("09/26/2020 01:30:00", "09/26/2020 02:00:00"));
        testIntervalParser(interval, "09/26/2020 00:40:00", "09/26/2020 01:30:00",
                List.of("09/26/2020 01:00:00", "09/26/2020 01:30:00"));
        testIntervalParser(interval, "09/26/2020 01:15:00", "09/26/2020 01:15:00", List.of());
    }

    @Test
    public void test_TimeIntervals_HOURS_1() throws Exception {
        TimeIntervals interval = TimeIntervals.HOURS_1;
        testIntervalParser(interval, "09/26/2020 01:00:00", "09/26/2020 02:15:00", List.of("09/26/2020 02:00:00"));
        testIntervalParser(interval, "09/26/2020 00:40:00", "09/26/2020 03:30:00",
                List.of("09/26/2020 01:00:00", "09/26/2020 02:00:00", "09/26/2020 03:00:00"));
        testIntervalParser(interval, "09/26/2020 01:15:00", "09/26/2020 02:00:00", List.of());
    }

    @Test
    public void test_TimeIntervals_DAYS_1() throws Exception {
        TimeIntervals interval = TimeIntervals.DAYS_1;
        testIntervalParser(interval, "09/26/2020 01:00:00", "09/29/2020 02:15:00",
                List.of("09/27/2020 00:00:00", "09/28/2020 00:00:00", "09/29/2020 00:00:00"));
        testIntervalParser(interval, "09/26/2020 00:40:00", "09/27/2020 03:30:00", List.of("09/27/2020 00:00:00"));
        testIntervalParser(interval, "09/27/2020 00:00:00", "09/27/2020 00:00:00", List.of());
        testIntervalParser(interval, "10/28/2020 00:00:00", "11/03/2020 00:00:00", List.of("10/29/2020 00:00:00", "10/30/2020 00:00:00",
                "10/31/2020 00:00:00", "11/01/2020 00:00:00", "11/02/2020 00:00:00", "11/03/2020 00:00:00"));
    }
    
    /**
     * Test Interval Parser
     * 
     * @param interval       - intervals to use 5,15,30 minutes, 1 hour or 1 day
     * @param start
     * @param stop
     * @param validIntervals - intervals to validate against
     */
    private void testIntervalParser(TimeIntervals interval, String start, String stop, List<String> validIntervals) {
        Instant startDate = DateTime.parse(start, format).toInstant();
        Instant stopDate = DateTime.parse(stop, format).toInstant();

        IntervalParser parser = new IntervalParser(startDate, stopDate, interval, dateFormattingService, YukonUserContext.system,
                log);
        TestIntervalParser tester = new TestIntervalParser();
        validIntervals.forEach(validInterval -> tester.addInterval(validInterval));
        tester.compare(parser);
    }

    private class TestIntervalParser {
        boolean hasValidInterval = true;
        List<Date> intervals = new ArrayList<>();
        Range<Instant> range;

        void addInterval(String date) {
            intervals.add(new Date(DateTime.parse(date, format).getMillis()));
        }

        // Compares hard coded intervals with the parsed intervals
        void compare(IntervalParser parser) {
            if (intervals.isEmpty()) {
                hasValidInterval = false;
            } else {
                range = Range.inclusive(new Instant(intervals.get(0)), new Instant(intervals.get(intervals.size() - 1)));
                if (range.getMin().isAfter(range.getMax())) {
                    hasValidInterval = false;
                }
                assertTrue(CollectionUtils.isEqualCollection(intervals, parser.getIntervals()), "Intervals are incorrect");
                assertTrue(range.equals(parser.getRange()), "Range is incorrect");
                assertTrue(hasValidInterval == parser.hasValidInterval(), "Interval count is invalid");
            }
        }
    }
}
