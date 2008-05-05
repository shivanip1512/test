package com.cannontech.core.service.impl;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.DurationFormattingService.DurationFormat;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;

public class DurationFormattingServiceImplTest {
    private DurationFormattingService service;
    
    @Before
    public void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("yukon.common.durationFormatting.pattern.DHMS", Locale.US, "{0} {0,choice,0#days|1#day|1<days} {1} {1,choice,0#hours|1#hour|1<hours} {2} {2,choice,0#minutes|1#minute|1<minutes} {3} {3,choice,0#seconds|1#second|1<seconds}");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.HMS", Locale.US, "{0} {0,choice,0#hours|1#hour|1<hours} {1} {1,choice,0#minutes|1#minute|1<minutes} {2} {2,choice,0#seconds|1#second|1<seconds}");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.HM", Locale.US, "{0} {0,choice,0#hours|1#hour|1<hours} {1} {1,choice,0#minutes|1#minute|1<minutes}");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.H", Locale.US, "{0} {0,choice,0#hours|1#hour|1<hours}");
        messageSource.addMessage("yukon.common.durationFormatting.pattern.M", Locale.US, "{0} {0,choice,0#minutes|1#minute|1<minutes}");
        
        YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
        messageSourceResolver.setMessageSource(messageSource);
        
        DurationFormattingServiceImpl serviceImpl = new DurationFormattingServiceImpl();
        serviceImpl.setMessageSourceResolver(messageSourceResolver);
        service = serviceImpl;
    }
    
    @After
    public void tearDown() {
        service = null;
    }
    
    @Test
    public void test_format_DHMS() {
        String expected = "1 day 0 hours 0 minutes 0 seconds";
        String result = service.formatDuration(86400, DurationFormat.DHMS, null);
        Assert.assertEquals(expected, result);
        
        expected = "1 day 1 hour 1 minute 1 second";
        result = service.formatDuration(90061, DurationFormat.DHMS, null);
        Assert.assertEquals(expected, result);
        
        expected = "0 days 15 hours 25 minutes 0 seconds";
        result = service.formatDuration(55500, DurationFormat.DHMS, null);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_HMS() {
        String expected = "0 hours 5 minutes 59 seconds";
        String result = service.formatDuration(359, DurationFormat.HMS, null);
        Assert.assertEquals(expected, result);
    
        expected = "2 hours 59 minutes 59 seconds";
        result = service.formatDuration(10799, DurationFormat.HMS, null);
        Assert.assertEquals(expected, result);

        expected = "25 hours 0 minutes 5 seconds";
        result = service.formatDuration(90005, DurationFormat.HMS, null);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_HM() {
        String expected = "0 hours 0 minutes";
        String result = service.formatDuration(0, DurationFormat.HM, null);
        Assert.assertEquals(expected, result);

        expected = "0 hours 59 minutes";
        result = service.formatDuration(3540, DurationFormat.HM, null);
        Assert.assertEquals(expected, result);
        
        expected = "1 hour 1 minute";
        result = service.formatDuration(3660, DurationFormat.HM, null);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_H() {
        String expected = "0 hours";
        String result = service.formatDuration(3540, DurationFormat.H, null);
        Assert.assertEquals(expected, result);
        
        expected = "1 hour";
        result = service.formatDuration(3600, DurationFormat.H, null);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void test_format_M() {
        String expected = "0 minutes";
        String result = service.formatDuration(0, DurationFormat.M, null);
        Assert.assertEquals(expected, result);
        
        expected = "800 minutes";
        result = service.formatDuration(48000, DurationFormat.M, null);
        Assert.assertEquals(expected, result);

        expected = "1 minute";
        result = service.formatDuration(60, DurationFormat.M, null);
        Assert.assertEquals(expected, result);
    }
    
}
