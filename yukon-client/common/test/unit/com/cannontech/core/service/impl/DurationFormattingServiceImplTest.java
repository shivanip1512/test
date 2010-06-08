package com.cannontech.core.service.impl;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;

public class DurationFormattingServiceImplTest {
    private DurationFormattingService service;
    private YukonUserContext userContext;
    
    @Before
    public void setUp() {
    	
        StaticMessageSource messageSource = new StaticMessageSource();
        
        messageSource.addMessage("yukon.common.durationFormatting.pattern.DHMS", Locale.US, "%D_FULL% %H_FULL% %M_FULL% %S_FULL%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.DH", Locale.US, "%D_FULL% %H_FULL%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.DH_ABBR", Locale.US, "%D_ABBR% %H_ABBR%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.HMS", Locale.US, "%H_FULL% %M_FULL% %S_FULL%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.HM", Locale.US, "%H_FULL% %M_FULL%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.H", Locale.US, "%H_FULL%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.M", Locale.US, "%M_FULL%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.HM_ABBR", Locale.US, "%H_ABBR% %M_ABBR%");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.HM_SHORT", Locale.US, "%H%:%M%");
        
        messageSource.addMessage("yukon.common.durationFormatting.symbol.S.suffix.singular", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.S.suffix.plural", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.S_FULL.suffix.singular", Locale.US, "second");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.S_FULL.suffix.plural", Locale.US, "seconds");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.S_ABBR.suffix.singular", Locale.US, "s");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.S_ABBR.suffix.plural", Locale.US, "s");
    	
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.M.suffix.singular", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.M.suffix.plural", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.M_FULL.suffix.singular", Locale.US, "minute");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.M_FULL.suffix.plural", Locale.US, "minutes");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.M_ABBR.suffix.singular", Locale.US, "m");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.M_ABBR.suffix.plural", Locale.US, "m");
    	
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.H.suffix.singular", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.H.suffix.plural", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.H_FULL.suffix.singular", Locale.US, "hour");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.H_FULL.suffix.plural", Locale.US, "hours");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.H_ABBR.suffix.singular", Locale.US, "h");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.H_ABBR.suffix.plural", Locale.US, "h");
    	
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.D.suffix.singular", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.D.suffix.plural", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.D_FULL.suffix.singular", Locale.US, "day");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.D_FULL.suffix.plural", Locale.US, "days");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.D_ABBR.suffix.singular", Locale.US, "d");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.D_ABBR.suffix.plural", Locale.US, "d");
    	
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.MO.suffix.singular", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.MO.suffix.plural", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.MO_FULL.suffix.singular", Locale.US, "month");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.MO_FULL.suffix.plural", Locale.US, "months");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.MO_ABBR.suffix.singular", Locale.US, "mo");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.MO_ABBR.suffix.plural", Locale.US, "mo");
    	
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.Y.suffix.singular", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.Y.suffix.plural", Locale.US, "");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.Y_FULL.suffix.singular", Locale.US, "year");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.Y_FULL.suffix.plural", Locale.US, "years");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.Y_ABBR.suffix.singular", Locale.US, "y");
    	messageSource.addMessage("yukon.common.durationFormatting.symbol.Y_ABBR.suffix.plural", Locale.US, "y");
        
        YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
        messageSourceResolver.setMessageSource(messageSource);
        
        DurationFormattingServiceImpl serviceImpl = new DurationFormattingServiceImpl();
        serviceImpl.setMessageSourceResolver(messageSourceResolver);
        service = serviceImpl;
        
        
        userContext = new SystemUserContext();
    }
    
    @After
    public void tearDown() {
        service = null;
    }
    
    @Test
    public void test_format_DHMS() {
        String expected = "1 day 0 hours 0 minutes 0 seconds";
        String result = service.formatDuration(86400, TimeUnit.SECONDS, DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1 day 1 hour 1 minute 1 second";
        result = service.formatDuration(90061, TimeUnit.SECONDS, DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1 day 1 hour 1 minute 1 second";
        result = service.formatDuration(90061000, TimeUnit.MILLISECONDS, DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1 day 1 hour 1 minute 2 seconds";
        result = service.formatDuration(90061500, TimeUnit.MILLISECONDS, DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 days 15 hours 25 minutes 0 seconds";
        result = service.formatDuration(55500, TimeUnit.SECONDS, DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_HMS() {
        String expected = "0 hours 5 minutes 59 seconds";
        String result = service.formatDuration(359, TimeUnit.SECONDS, DurationFormat.HMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 hours 5 minutes 59 seconds";
        result = service.formatDuration(359, TimeUnit.SECONDS, DurationFormat.HMS, false, userContext);
        Assert.assertEquals(expected, result);
    
        expected = "2 hours 59 minutes 59 seconds";
        result = service.formatDuration(10799, TimeUnit.SECONDS, DurationFormat.HMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "2 hours 59 minutes 59 seconds";
        result = service.formatDuration(10799, TimeUnit.SECONDS, DurationFormat.HMS, false, userContext);
        Assert.assertEquals(expected, result);

        expected = "25 hours 0 minutes 5 seconds";
        result = service.formatDuration(90005, TimeUnit.SECONDS, DurationFormat.HMS, userContext);
        Assert.assertEquals(expected, result);

        expected = "0 hours 0 minutes 0 seconds";
        result = service.formatDuration(499, TimeUnit.MILLISECONDS, DurationFormat.HMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 hours 0 minutes 1 second";
        result = service.formatDuration(500, TimeUnit.MILLISECONDS, DurationFormat.HMS, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 hours 0 minutes 0 seconds";
        result = service.formatDuration(500, TimeUnit.MILLISECONDS, DurationFormat.HMS, false, userContext);
        Assert.assertEquals(expected, result);

        expected = "0 hours 0 minutes 1 second";
        result = service.formatDuration(500, TimeUnit.MILLISECONDS, DurationFormat.HMS, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_HM() {
        String expected = "0 hours 0 minutes";
        String result = service.formatDuration(0, TimeUnit.SECONDS, DurationFormat.HM, userContext);
        Assert.assertEquals(expected, result);
        
        result = service.formatDuration(29, TimeUnit.SECONDS, DurationFormat.HM, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 hours 0 minutes";
        result = service.formatDuration(30, TimeUnit.SECONDS, DurationFormat.HM, false, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1 hour 1 minute";
        result = service.formatDuration(3630, TimeUnit.SECONDS, DurationFormat.HM, userContext);
        Assert.assertEquals(expected, result);

        expected = "0 hours 1 minute";
        result = service.formatDuration(30, TimeUnit.SECONDS, DurationFormat.HM, userContext);
        Assert.assertEquals(expected, result);

        expected = "0 hours 59 minutes";
        result = service.formatDuration(3540, TimeUnit.SECONDS, DurationFormat.HM, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 hours 59 minutes";
        result = service.formatDuration(3540, TimeUnit.SECONDS, DurationFormat.HM, false, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1 hour 0 minutes";
        result = service.formatDuration(3540 + 30, TimeUnit.SECONDS, DurationFormat.HM, true, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1 hour 1 minute";
        result = service.formatDuration(3660, TimeUnit.SECONDS, DurationFormat.HM, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_H() {
        String expected = "0 hours";
        String result = service.formatDuration(1799, TimeUnit.SECONDS, DurationFormat.H, userContext);
        Assert.assertEquals(expected, result);

        expected = "1 hour";
        result = service.formatDuration(1800, TimeUnit.SECONDS, DurationFormat.H, userContext);
        Assert.assertEquals(expected, result);

        result = service.formatDuration(3540, TimeUnit.SECONDS, DurationFormat.H, userContext);
        Assert.assertEquals(expected, result);

        result = service.formatDuration(3600, TimeUnit.SECONDS, DurationFormat.H, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_M() {
        String expected = "0 minutes";
        String result = service.formatDuration(0, TimeUnit.SECONDS, DurationFormat.M, userContext);
        Assert.assertEquals(expected, result);

        result = service.formatDuration(29, TimeUnit.SECONDS, DurationFormat.M, userContext);
        Assert.assertEquals(expected, result);

        expected = "800 minutes";
        result = service.formatDuration(48000, TimeUnit.SECONDS, DurationFormat.M, userContext);
        Assert.assertEquals(expected, result);

        expected = "1 minute";
        result = service.formatDuration(30, TimeUnit.SECONDS, DurationFormat.M, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 minutes";
        result = service.formatDuration(59, TimeUnit.SECONDS, DurationFormat.M, false, userContext);
        Assert.assertEquals(expected, result);

        expected = "1 minute";
        result = service.formatDuration(60, TimeUnit.SECONDS, DurationFormat.M, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_HM_SHORT() {
    	
        String expected = "01:30";
        String result = service.formatDuration(90, TimeUnit.MINUTES, DurationFormat.HM_SHORT, userContext);
        Assert.assertEquals(expected, result);

        expected = "01:29";
        result = service.formatDuration(5371, TimeUnit.SECONDS, DurationFormat.HM_SHORT, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "01:30";
        result = service.formatDuration(3600 + 1800, TimeUnit.SECONDS, DurationFormat.HM_SHORT, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "01:30";
        result = service.formatDuration(3600 + 1800, TimeUnit.SECONDS, DurationFormat.HM_SHORT, true, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "01:30";
        result = service.formatDuration(3600 + 1800 + 31, TimeUnit.SECONDS, DurationFormat.HM_SHORT, false, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "01:31";
        result = service.formatDuration(3600 + 1800 + 31, TimeUnit.SECONDS, DurationFormat.HM_SHORT, true, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_DH() {
    	
        String expected = "0 days 1 hour";
        String result = service.formatDuration(60, TimeUnit.MINUTES, DurationFormat.DH, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0 days 2 hours";
        result = service.formatDuration(90, TimeUnit.MINUTES, DurationFormat.DH, userContext);
        Assert.assertEquals(expected, result);

        expected = "7 days 0 hours";
        result = service.formatDuration(604800000, TimeUnit.MILLISECONDS, DurationFormat.DH, true, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_DH_ABBR() {
    	
        String expected = "1d 12h";
        String result = service.formatDuration(36, TimeUnit.HOURS, DurationFormat.DH_ABBR, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0d 2h";
        result = service.formatDuration(90, TimeUnit.MINUTES, DurationFormat.DH_ABBR, userContext);
        Assert.assertEquals(expected, result);

        expected = "7d 0h";
        result = service.formatDuration(604800000, TimeUnit.MILLISECONDS, DurationFormat.DH_ABBR, true, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_HM_ABBR() {
    	
        String expected = "0h 0m";
        String result = service.formatDuration(0, TimeUnit.SECONDS, DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);
        
        result = service.formatDuration(29, TimeUnit.SECONDS, DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0h 0m";
        result = service.formatDuration(30, TimeUnit.SECONDS, DurationFormat.HM_ABBR, false, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1h 1m";
        result = service.formatDuration(3630, TimeUnit.SECONDS, DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);

        expected = "0h 1m";
        result = service.formatDuration(30, TimeUnit.SECONDS, DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);

        expected = "0h 59m";
        result = service.formatDuration(3540, TimeUnit.SECONDS, DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0h 59m";
        result = service.formatDuration(3540, TimeUnit.SECONDS, DurationFormat.HM_ABBR, false, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "0h 59m";
        result = service.formatDuration(3540 + 30, TimeUnit.SECONDS, DurationFormat.HM_ABBR, false, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1h 0m";
        result = service.formatDuration(3540 + 30, TimeUnit.SECONDS, DurationFormat.HM_ABBR, true, userContext);
        Assert.assertEquals(expected, result);
        
        expected = "1h 1m";
        result = service.formatDuration(3660, TimeUnit.SECONDS, DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_start_end() {
    	
    	DateTime startDateTime = new DateTime(2010, 1, 1, 9, 0, 0, 0, userContext.getJodaTimeZone());
        DateTime stopDateTime = new DateTime(2010, 1, 1, 11, 20, 0, 0, userContext.getJodaTimeZone());
        String expected = "2h 20m";
        String result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.HM_ABBR, userContext);
        Assert.assertEquals(expected, result);
        
        startDateTime = new DateTime(2010, 1, 1, 10, 0, 0, 0, userContext.getJodaTimeZone());
        stopDateTime = new DateTime(2010, 1, 6, 11, 0, 59, 500, userContext.getJodaTimeZone());
        expected = "5 days 1 hour 1 minute 0 seconds";
        result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);

        startDateTime = new DateTime(2010, 1, 1, 10, 0, 0, 0, userContext.getJodaTimeZone());
        stopDateTime = new DateTime(2010, 1, 6, 11, 0, 59, 0, userContext.getJodaTimeZone());
        expected = "5 days 1 hour 0 minutes 59 seconds";
        result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.DHMS, false, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_start_end_DST_sensitive() {
    	
    	// ends during DST day - lose an hour, only 3 hours
    	DateTime startDateTime = new DateTime(2010, 3, 3, 23, 0, 0, 0, userContext.getJodaTimeZone());
    	DateTime stopDateTime = new DateTime(2010, 3, 14, 3, 0, 0, 0, userContext.getJodaTimeZone());
    	String expected = "10 days 3 hours 0 minutes 0 seconds";
    	String result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
        
        // ends after DST day
        startDateTime = new DateTime(2010, 3, 3, 23, 0, 0, 0, userContext.getJodaTimeZone());
        stopDateTime = new DateTime(2010, 3, 15, 3, 0, 0, 0, userContext.getJodaTimeZone());
        expected = "11 days 4 hours 0 minutes 0 seconds";
        result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_start_end_DST_and_userContext_sensitive() {
        
        // crosses "system" (America/Chicago) DST but user is GMT, DST should have no effect
        LiteYukonUser user2 = new LiteYukonUser(-1);
        SimpleYukonUserContext userContext_gmt = new SimpleYukonUserContext(user2, Locale.US, DateTimeZone.forID("GMT").toTimeZone(), "");
        
        // dates in "system" TZ
        DateTime startDateTime = new DateTime(2010, 3, 3, 23, 0, 0, 0, userContext.getJodaTimeZone());
        DateTime stopDateTime = new DateTime(2010, 3, 15, 3, 20, 19, 18, userContext.getJodaTimeZone());
        
        // user in same TZ as system
        String expected = "11 days 4 hours 20 minutes 19 seconds";
        String result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.DHMS, userContext);
        Assert.assertEquals(expected, result);
        
        // user in different TZ than system
        expected = "11 days 3 hours 20 minutes 19 seconds";
        result = service.formatDuration(startDateTime.toDate(), stopDateTime.toDate(), DurationFormat.DHMS, userContext_gmt);
        Assert.assertEquals(expected, result);
    }
    
}
