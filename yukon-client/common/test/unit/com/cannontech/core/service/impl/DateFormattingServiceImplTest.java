package com.cannontech.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.Signal;

public class DateFormattingServiceImplTest {
    
    private DateFormattingServiceImpl impl;
    
    private DateFormat standardFormat = SimpleDateFormat.getInstance();


    @Before
    public void setUp() throws Exception {
        impl = new DateFormattingServiceImpl();
        impl.setYukonUserDao(new YukonUserDao() {

            public LiteContact getLiteContact(int userID_) {
                throw new UnsupportedOperationException();
            }

            public LiteYukonUser getLiteYukonUser(int userID_) {
                throw new UnsupportedOperationException();
            }

            public LiteYukonUser getLiteYukonUser(String userName_) {
                throw new UnsupportedOperationException();
            }

            public TimeZone getUserTimeZone(LiteYukonUser user) {
                return TimeZone.getDefault();
            }
            
        });
        

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
                Date date = impl.flexibleDateParser(pair.userInput, new LiteYukonUser());
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
