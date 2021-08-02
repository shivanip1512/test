package com.cannontech.amr.macsscheduler.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.MethodSource;

import com.cannontech.amr.macsscheduler.model.MacsTimeField.AmPmOptionEnum;

public class MacsTimeFieldTest {

    @Nested
    public static class TestGetTimeStrings {
    
        public String expected;
        public Integer hours;
        public Integer minutes;
        public AmPmOptionEnum meridiem;
    
        public static Collection<Object[]> existingStates() {
            final var Am = AmPmOptionEnum.AM;
            final var Pm = AmPmOptionEnum.PM;
            return List.of(
                    new Object[] { "00:00:00", 0, 0, Am },
                    new Object[] { "00:01:00", 0, 1, Am },
                    new Object[] { "00:00:00", 12, 0, Am },
                    new Object[] { "00:34:00", 12, 34, Am },
                    new Object[] { "11:59:00", 11, 59, Am },

                    new Object[] { "12:00:00", 0, 0, Pm },
                    new Object[] { "12:01:00", 0, 1, Pm },
                    new Object[] { "12:00:00", 12, 0, Pm },
                    new Object[] { "12:34:00", 12, 34, Pm },
                    new Object[] { "23:59:00", 11, 59, Pm }
                    );
        }
    
        @ParameterizedTest
        @MethodSource("existingStates")
        public void testGetTimeString(ArgumentsAccessor argumentsAccessor) {
            expected = argumentsAccessor.getString(0);
            hours = argumentsAccessor.getInteger(1);
            minutes = argumentsAccessor.getInteger(2);
            meridiem = (AmPmOptionEnum) argumentsAccessor.get(3);
            var m = new MacsTimeField();
            m.setHours(hours);
            m.setMinutes(minutes);
            m.setAmPm(meridiem);
            
            assertEquals(expected, m.getTimeString());
        }
    }

    @Nested
    public static class Others {
        @Test
        public void testGetTimeField() {
            DateTime dt = DateTime.parse("2021-04-09T12:34:56");
            var m = MacsTimeField.getTimeField(dt);
            
            assertEquals(12, m.getHours());
            assertEquals(34, m.getMinutes());
            assertEquals(AmPmOptionEnum.PM, m.getAmPm());
            assertEquals("12:34:00", m.getTimeString());
    
            dt = DateTime.parse("2021-04-09T00:00:00");
            m = MacsTimeField.getTimeField(dt);
            
            assertEquals(12, m.getHours());
            assertEquals(0, m.getMinutes());
            assertEquals(AmPmOptionEnum.AM, m.getAmPm());
            assertEquals("00:00:00", m.getTimeString());
        }
    
        @Test
        public void testParseDate() {
            var dt = MacsTimeField.parseDate(2021, 4, 9, "12:34:56");
            
            assertEquals(2021, dt.getYear());
            assertEquals(4, dt.getMonthOfYear());
            assertEquals(9, dt.getDayOfMonth());
            assertEquals(12, dt.getHourOfDay());
            assertEquals(34, dt.getMinuteOfHour());
            assertEquals(56, dt.getSecondOfMinute());
    
            dt = MacsTimeField.parseDate(2021, 4, 9, "00:00:00");
            
            assertEquals(2021, dt.getYear());
            assertEquals(4, dt.getMonthOfYear());
            assertEquals(9, dt.getDayOfMonth());
            assertEquals(0, dt.getHourOfDay());
            assertEquals(0, dt.getMinuteOfHour());
            assertEquals(0, dt.getSecondOfMinute());
        }
    }
}
