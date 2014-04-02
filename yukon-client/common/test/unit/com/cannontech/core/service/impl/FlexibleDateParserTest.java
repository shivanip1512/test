package com.cannontech.core.service.impl;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cannontech.core.service.DateFormattingService.DateOnlyMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/com/cannontech/mockContext.xml","/com/cannontech/core/service/dateContext.xml"})
public class FlexibleDateParserTest {
    
    @Resource(name="mdyDateParser")
    private FlexibleDateParser mdy_en;
    
    @Resource(name="ymdDateParser")
    private FlexibleDateParser ymd_en;
    
    //Using this DateFormat so that the control date and time are being parsed the same regardless of locale
    private DateFormat standardFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");

    @Test
    public void testFlexibleDateParserYMD_en() {
        // build up our list
        List<DatePair> pairs = new ArrayList<DatePair>();
        try {
            pairs.add(new DatePair("1978-03-07 1:00PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("1978-03-07 1:00 PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("1978-3-7 1:00 PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("1978-3-7 1:00PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("1978-03-7 13:00", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("1978-03-7 1:00", "03/07/1978 1:00 AM"));
            pairs.add(new DatePair("1978-03-7", "03/07/1978 12:00 AM"));
            pairs.add(new DatePair("1978-03-07", "03/07/1978 12:00 AM"));
        } catch (ParseException e) {
            fail("unable to initialize list");
        }
        
        Locale testLocale = LocaleUtils.toLocale("en_US");
        TimeZone testTimeZone = TimeZone.getDefault(); // must match timezone of standardFormat
        
        testPairs(ymd_en, pairs, testLocale, testTimeZone);
        
    }

    @Test
    public void testFlexibleDateParserMDY_en() {
        // build up our list
        List<DatePair> pairs = new ArrayList<DatePair>();
        try {
            pairs.add(new DatePair("03/07/1978 1:00PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("03/07/1978 1:00 PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/1978 1:00 PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/1978 1:00PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00 PM", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00 pm", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00 am", "03/07/1978 1:00 AM"));
            pairs.add(new DatePair("3/7/78 1:00a", "03/07/1978 1:00 AM"));
            pairs.add(new DatePair("3/7/78 1:00p", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00 a", "03/07/1978 1:00 AM"));
            pairs.add(new DatePair("3/7/78 1:00 p", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00P", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 1:00 P", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("3/7/78 13:00", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("03/7/1978 13:00", "03/07/1978 1:00 PM"));
            pairs.add(new DatePair("03/7/1978 1:00", "03/07/1978 1:00 AM"));
            pairs.add(new DatePair("03/7/1978", "03/07/1978 12:00 AM"));
            pairs.add(new DatePair("03/7/78", "03/07/1978 12:00 AM"));
            pairs.add(new DatePair("3/7/78", "03/07/1978 12:00 AM"));
            pairs.add(new DatePair("03/07/1978", "03/07/1978 12:00 AM"));
        } catch (ParseException e) {
            fail("unable to initialize list");
        }
        
        Locale testLocale = LocaleUtils.toLocale("en_US");
        TimeZone testTimeZone = TimeZone.getDefault(); // must match timezone of standardFormat
        
        testPairs(mdy_en, pairs, testLocale, testTimeZone);

    }

    private void testPairs(FlexibleDateParser parser, List<DatePair> pairs, Locale testLocale, TimeZone testTimeZone) {
        for (DatePair pair : pairs) {
            try {
                Date date = parser.parseDate(pair.userInput, DateOnlyMode.START_OF_DAY, testLocale, testTimeZone);
                assertEquals(pair.userInput + " doesn't match expected value", pair.fullDate, date);
            } catch (ParseException e) {
                fail("unable to parse date: " + pair.userInput);
            }
        }
    }
    
    public class DatePair {
        String userInput;
        Date fullDate;
        public DatePair(String userInput, String fullSpec) throws ParseException {
            this.userInput = userInput;
            fullDate = standardFormat.parse(fullSpec);
        }
    }

}
