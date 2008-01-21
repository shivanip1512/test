package com.cannontech.core.service.impl;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.user.SystemUserContext;

public class DateFormattingServiceImplTest {
    
    private DateFormattingServiceImpl impl;
    
    private DateFormat standardFormat = SimpleDateFormat.getInstance();


    @Before
    public void setUp() throws Exception {
        impl = new DateFormattingServiceImpl();

    }

    @Test
    public void testFlexibleDateParserStringLiteYukonUser() {
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
        
        for (DatePair pair : pairs) {
            try {
                Date date = impl.flexibleDateParser(pair.userInput, new SystemUserContext());
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
