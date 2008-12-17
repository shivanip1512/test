package com.cannontech.core.service.impl;

import java.util.Date;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public strictfp class DurationFormattingServiceImpl implements DurationFormattingService {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public String formatDuration(final long duration, final DurationFormat type,
            final YukonUserContext yukonUserContext) {
    	
    	Date now = new Date();
    	long startInstant = now.getTime();
    	long endInstant = startInstant + duration;

        MessageSourceAccessor messageSourceAccessor = 
        	messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        Object[] args = getArgs(startInstant, endInstant, type);
        
        String result = messageSourceAccessor.getMessage(type, args); 
        return result;
    }
    

	@Override
	public String formatDuration(Date startDate, Date endDate,
			DurationFormat type, YukonUserContext yukonUserContext) {
		
		
		MessageSourceAccessor messageSourceAccessor = 
			messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
		
		Object[] args = getArgs(startDate.getTime(), endDate.getTime(), type);
		
		String result = messageSourceAccessor.getMessage(type, args); 
		return result;
	}

    
    private Object[] getArgs(long startInstant, long endInstant, DurationFormat type) {
    	// Create a Joda period

    	Period period;
    	switch (type) {
            case DHMS : {
				period = new Period(startInstant, endInstant, PeriodType.dayTime());
                return new Object[] { 
                		period.get(DurationFieldType.days()), 
                		period.get(DurationFieldType.hours()), 
                		period.get(DurationFieldType.minutes()), 
                		period.get(DurationFieldType.seconds())};
            }
            case DH : {
            	period = new Period(startInstant, endInstant, PeriodType.dayTime());
            	return new Object[] { 
            			period.get(DurationFieldType.days()), 
            			period.get(DurationFieldType.hours())};
            }

            case HMS : {
            	period = new Period(startInstant, endInstant, PeriodType.time());
            	return new Object[] { 
            			period.get(DurationFieldType.hours()), 
            			period.get(DurationFieldType.minutes()), 
            			period.get(DurationFieldType.seconds())};
            }

            case HM : {
            	period = new Period(startInstant, endInstant, PeriodType.time());
            	return new Object[] { 
            			period.get(DurationFieldType.hours()), 
            			period.get(DurationFieldType.minutes())};
            }

            case H : {
            	period = new Period(startInstant, endInstant, PeriodType.hours());
            	return new Object[] { 
            			period.get(DurationFieldType.hours())};
            }
            
            case M : {
            	period = new Period(startInstant, endInstant, PeriodType.minutes());
            	return new Object[] { 
            			period.get(DurationFieldType.minutes())};
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
