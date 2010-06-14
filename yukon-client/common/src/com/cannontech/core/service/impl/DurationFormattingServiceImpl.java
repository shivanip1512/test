package com.cannontech.core.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Chronology;
import org.joda.time.Duration;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.core.service.durationFormatter.DurationFormatSymbol;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public strictfp class DurationFormattingServiceImpl implements DurationFormattingService {
    
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	private static final char SYMBOL_DELIM = '%';
    
    // DURATION VALUE
    public String formatDuration(final long durationValue, final TimeUnit durationUnit, final DurationFormat type, final YukonUserContext yukonUserContext) {
    	
    	PeriodGenerator periodGenerator = new PeriodGenerator() {
			@Override
			public Period generatePeriod(PeriodType periodType) {
				long durationMillis = TimeUnit.MILLISECONDS.convert(durationValue, durationUnit);
		    	Duration duration = new Duration(durationMillis);
		    	Period period = periodGenerate(duration, periodType, type.getRoundingMode(), yukonUserContext);
		    	return period;
			}
		};
    	
		return getFormattedDuration(type, periodGenerator, yukonUserContext);
    }
    
    // START/END DATE
    @Override
	public String formatDuration(final Date startDate, final Date endDate, final DurationFormat type, final YukonUserContext yukonUserContext) {
		
    	PeriodGenerator periodGenerator = new PeriodGenerator() {
			@Override
			public Period generatePeriod(PeriodType periodType) {
		    	Interval interval = new Interval(startDate.getTime(), endDate.getTime());
            	Period period = periodGenerate(interval.getStart(), interval.toDuration(), periodType, type.getRoundingMode(), yukonUserContext);
		    	return period;
			}
		};
    	
		return getFormattedDuration(type, periodGenerator, yukonUserContext);
	}

    // START/END READABLE INSTANT
    @Override
    public String formatDuration(final ReadableInstant startDate, final ReadableInstant endDate, final DurationFormat type, final YukonUserContext yukonUserContext) {
        
        PeriodGenerator periodGenerator = new PeriodGenerator() {
            @Override
            public Period generatePeriod(PeriodType periodType) {
            	Interval interval = new Interval(startDate, endDate);
            	Period period = periodGenerate(interval.getStart(), interval.toDuration(), periodType, type.getRoundingMode(), yukonUserContext);
                return period;
            }
        };
        
        return getFormattedDuration(type, periodGenerator, yukonUserContext);
    }
    
    private String getFormattedDuration(DurationFormat type, PeriodGenerator periodGenerator, YukonUserContext yukonUserContext) {
    	
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
    	
		// init builder
		PeriodFormatterBuilder periodFormatterBuilder = new PeriodFormatterBuilder();
		periodFormatterBuilder.printZeroAlways();
		
		// parse Format
        String format = messageSourceAccessor.getMessage(type); 
    	List<String> parsedFormat = parseFormat(format);
		
    	// Get durationFieldTypesList
    	List<DurationFieldType> durationFieldTypesList = Lists.newArrayList();
    	for (String subFormat : parsedFormat) {
    	
    		if (StringUtils.isEmpty(subFormat)) {
				continue;
			}
    		
    		// apply symbol appender 
    		if (isDurationFormatSymbolSubstring(subFormat)) {
    		
    			String symbolStr = subFormat.substring(1, subFormat.length() -1);
    			DurationFormatSymbol symbol = DurationFormatSymbol.valueOf(String.valueOf(symbolStr));
    			durationFieldTypesList.add(symbol.getDurationFieldType());
    		
    			String singularSuffix = messageSourceAccessor.getMessage(symbol.getSingularSuffixKey());
				String pluralSuffix = messageSourceAccessor.getMessage(symbol.getPluralSuffixKey());
				symbol.applyAppenderToBuilder(periodFormatterBuilder, singularSuffix, pluralSuffix);
    		
			// append literal
    		} else {
    			periodFormatterBuilder.appendLiteral(subFormat);
    		}
    	}
		
    	// period - only create a Period that supports the DurationFieldTypes we intend to display in the formated string
    	// Using a Period with all the standard fields will cause the millis to get dispersed from left to right and we may lose them if those left side fields are not used in the format.
    	PeriodType periodType = PeriodType.forFields(durationFieldTypesList.toArray(new DurationFieldType[durationFieldTypesList.size()]));
		
    	// formatter
		PeriodFormatter periodFormatter = periodFormatterBuilder.toFormatter().withLocale(yukonUserContext.getLocale());
    	Period period = periodGenerator.generatePeriod(periodType);
    	
    	// format
    	StringBuffer out = new StringBuffer();
    	periodFormatter.printTo(out, period);
    	return out.toString();
    }

    
    private interface PeriodGenerator {
    	public Period generatePeriod(PeriodType periodType);
    }
    
    
    // HELPERS
    /**
     * Break the raw format string into substrings of symbols and literals
     */
    private static List<String> parseFormat(String format) {
    	
    	List<String> parsedFormat = Lists.newArrayList();
    	StringBuffer temp = new StringBuffer();
    	boolean inDelim = false;
    	for (char c : format.toCharArray()) {
    		
    		if (c == SYMBOL_DELIM && !inDelim) {
    			parsedFormat.add(temp.toString());
    			temp = new StringBuffer();
    			temp.append(c);
    			inDelim = true;
    		} else if (c == SYMBOL_DELIM && inDelim) {
    			temp.append(c);
    			parsedFormat.add(temp.toString());
    			temp = new StringBuffer();
    			inDelim = false;
    		} else {
    			temp.append(c);
    		}
    	}
    	parsedFormat.add(temp.toString());
    	
    	return parsedFormat;
	}
    
    private boolean isDurationFormatSymbolSubstring(String s) {

    	if (s.length() >= 3 && s.charAt(0) == SYMBOL_DELIM && s.charAt(s.length() - 1) == SYMBOL_DELIM) {
    		try {
    			DurationFormatSymbol.valueOf(s.substring(1, s.length() -1));
    			return true;
    		} catch (IllegalArgumentException e) {
    			// fall through
    		}
    	}
    	return false;
    }
    
    private static Period adjustPeriod(Period period, Duration duration, PeriodType periodType, RoundingMode roundingMode) {
    	
        DurationFieldType lastFieldType = periodType.getFieldType(periodType.size() - 1);
        ISOChronology chrono = ISOChronology.getInstanceUTC(); // probably want to pass this in too
        DurationField lastField = lastFieldType.getField(chrono);
        long lastUnitMillis = lastField.getUnitMillis();
        
        BigDecimal bigDuration = new BigDecimal(duration.getMillis());
        BigDecimal bigMillis = new BigDecimal(lastUnitMillis);
        
        BigDecimal remainder = bigDuration.remainder(bigMillis);
        BigDecimal adjustment = remainder.divide(bigMillis, 0, roundingMode);
        
        // the normalize on here deals with redistribution of millis after rounding. Ex: 59s 500ms -> 1m 0s 0m 0ms
        Period result = period.withFieldAdded(lastFieldType, adjustment.toBigIntegerExact().intValue()).normalizedStandard(periodType);
        return result;
    }
    
    private static Period periodGenerate(Duration duration, PeriodType periodType, RoundingMode roundingMode, YukonUserContext yukonUserContext) {
    	
    	// normalizing is only useful here because it gives us days and weeks, no other benefit
        Period normalizedStandard = new Period(duration, periodType, getChronologyForUser(yukonUserContext)).normalizedStandard(periodType);
        Period result = adjustPeriod(normalizedStandard, duration, periodType, roundingMode);
        return result;
    }
    
    private static Period periodGenerate(ReadableInstant startTime, Duration duration, PeriodType periodType, RoundingMode roundingMode, YukonUserContext yukonUserContext) {
    	
    	Interval interval = new Interval(startTime, duration);
        Period period = new Period(interval, periodType, getChronologyForUser(yukonUserContext));
        Period result = adjustPeriod(period, duration, periodType, roundingMode);
        return result;
    }

    
    private static Chronology getChronologyForUser(YukonUserContext yukonUserContext) {
    	return ISOChronology.getInstance(yukonUserContext.getJodaTimeZone());
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

}
