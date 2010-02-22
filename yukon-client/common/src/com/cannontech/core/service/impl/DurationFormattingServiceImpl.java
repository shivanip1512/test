package com.cannontech.core.service.impl;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.chrono.ISOChronology;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public strictfp class DurationFormattingServiceImpl implements DurationFormattingService {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;
    private static final int MILLIS_IN_MINUTE = 1000 * 60;
    private static final int MILLIS_IN_SECOND = 1000;
    
    @Override
    public String formatDuration(final long duration, TimeUnit unit, final DurationFormat type,
            final YukonUserContext yukonUserContext) {

    	long tempDuration = TimeUnit.MILLISECONDS.convert(duration, unit);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int milliSeconds = 0;
        
        // Get the hours, mins, secs using simple math - ignores time zones and daylight savings
     	switch (type) {
            case DHMS : 
            case DH : 
            	days = (int) (tempDuration / MILLIS_IN_DAY);
            	tempDuration = tempDuration - (MILLIS_IN_DAY * days);

            	hours = (int) (tempDuration / MILLIS_IN_HOUR);
            	tempDuration = tempDuration - (MILLIS_IN_HOUR * hours);
            	
            	minutes = (int) (tempDuration / MILLIS_IN_MINUTE);
            	tempDuration = tempDuration - (MILLIS_IN_MINUTE * minutes);
            	
            	seconds = (int) ((tempDuration + 500) / MILLIS_IN_SECOND);
            	break;
            case HMS :
            case HM :
            case HM_ABBR :
            case HM_SHORT : 
            case H : 
            	hours = (int) (tempDuration / MILLIS_IN_HOUR);
            	tempDuration = tempDuration - (MILLIS_IN_HOUR * hours);
            	
            	minutes = (int) (tempDuration / MILLIS_IN_MINUTE);
            	tempDuration = tempDuration - (MILLIS_IN_MINUTE * minutes);

            	seconds = (int) ((tempDuration + 500) / MILLIS_IN_SECOND);
            	break;
            case M : 
            	minutes = (int) (tempDuration / (1000 * 60));
            	tempDuration = tempDuration - (MILLIS_IN_MINUTE * minutes);
            	
            	seconds = (int) ((tempDuration + 500) / MILLIS_IN_SECOND);
            	break;
            default : throw new UnsupportedOperationException("Unsupported DurationFormat: " + type);
        }
     	
        Period period = new Period(years, months, weeks, days, hours, minutes, seconds, milliSeconds);
        
		Object[] args = getArgs(period, type);
        
		MessageSourceAccessor messageSourceAccessor = 
			messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        String result = messageSourceAccessor.getMessage(type, args); 
        return result;
    }
    

	@Override
	public String formatDuration(Date startDate, Date endDate,
			DurationFormat type, YukonUserContext yukonUserContext) {
		
		
		MessageSourceAccessor messageSourceAccessor = 
			messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
		
		TimeZone timeZone = yukonUserContext.getTimeZone();
		DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
    	Chronology chronology = ISOChronology.getInstance(dateTimeZone);
    	
    	// Create the correct type of period based on time zone, start and end instant
    	long startInstant = startDate.getTime();
    	long endInstant = endDate.getTime();
    	Period period;
     	switch (type) {
            case DHMS : 
            case DH : 
            	period = new Period(startInstant, endInstant, PeriodType.dayTime(), chronology);
            	break;
            case HMS :
            case HM : 
            	period = new Period(startInstant, endInstant, PeriodType.time(), chronology);
            	break;
            case H : 
            	period = new Period(startInstant, endInstant, PeriodType.hours(), chronology);
            	break;
            case M : 
            	period = new Period(startInstant, endInstant, PeriodType.minutes(), chronology);
            	break;
            default : throw new UnsupportedOperationException("Unsupported DurationFormat: " + type);
        }
		
		Object[] args = getArgs(period, type);
		
		// Format time period
		String result = messageSourceAccessor.getMessage(type, args); 
		return result;
	}

    /**
     * Helper method to get the correct arguments from the period based on the type of format
     * @param period - Period containing time field data
     * @param type - Type of format requested
     * @return Array of time field data
     */
    private Object[] getArgs(Period period, DurationFormat type) {
    	// Create a Joda period

        int days = period.get(DurationFieldType.days());
        int hours = period.get(DurationFieldType.hours());
        int minutes = period.get(DurationFieldType.minutes());
        int seconds = period.get(DurationFieldType.seconds());

        // do appropriate rounding
        if (type == DurationFormat.DHMS || type == DurationFormat.HMS) {
            if (period.get(DurationFieldType.millis()) >= 500) {
                seconds++;
            }
        } else if (type == DurationFormat.HM || type == DurationFormat.M) {
            if (seconds >= 30) {
                minutes++;
            }
        } else if (type == DurationFormat.DH || type == DurationFormat.H) {
            if (minutes >= 30) {
                hours++;
            }
        }

        switch (type) {
            case DHMS : {
                return new Object[] { 
                		days, 
                		hours, 
                		minutes, 
                		seconds};
            }
            case DH : {
            	return new Object[] { 
            			days, 
            			hours};
            }

            case HMS : {
            	return new Object[] { 
            	        hours, 
            			minutes, 
            			seconds};
            }
            
            case HM_SHORT :
            case HM_ABBR :
            case HM : {
            	return new Object[] { 
            	        hours, 
            	        minutes};
            }

            case H : {
            	return new Object[] { 
            	        hours};
            }
            
            case M : {
            	return new Object[] { 
            	        minutes};
            }
            
            default : throw new UnsupportedOperationException("Unsupported DurationFormat: " + type);
        }
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
