package com.cannontech.core.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public strictfp class DurationFormattingServiceImpl implements DurationFormattingService {
    
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	private static final char SYMBOL_DELIM = '%';
	
	private Set<DurationFieldType> unsupportedFieldsForDurationBasedFormatting;
	
	@PostConstruct
	public void init() {
		
		unsupportedFieldsForDurationBasedFormatting = ImmutableSet.of(DurationFieldType.centuries(), 
																	DurationFieldType.eras(), 
																	DurationFieldType.halfdays(), 
																	DurationFieldType.months(), 
																	DurationFieldType.weekyears(), 
																	DurationFieldType.years());
		
	}
	
	@Override
	public String formatPeriod(final ReadablePeriod period, final DurationFormat type, final YukonUserContext yukonUserContext) {
	    return getFormattedDuration(type, new PeriodGenerator() {
	        @Override
	        public Period generatePeriod(PeriodType periodType) {
	            return period.toPeriod().normalizedStandard(periodType);
	        }
	    }, yukonUserContext);
	}
	
	@Override
	public String formatDuration(final ReadableDuration duration, final DurationFormat type, final YukonUserContext yukonUserContext) {
	    return formatDuration(duration.getMillis(), TimeUnit.MILLISECONDS, type, yukonUserContext);
	}

    // DURATION VALUE
    public String formatDuration(final long durationValue, final TimeUnit durationUnit, final DurationFormat type, final YukonUserContext yukonUserContext) {
    	
    	PeriodGenerator periodGenerator = new PeriodGenerator() {
			@Override
			public Period generatePeriod(PeriodType periodType) {
				
				checkPeriodTypeForUnsupportedDurationFields(periodType);
				
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
//		periodFormatterBuilder.printZeroAlways();
		
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
    
    // imprecise duration fields are not supported when using a duration for formatting.
	// only a true interval populates these imprecise fields.
	private void checkPeriodTypeForUnsupportedDurationFields(PeriodType periodType) throws IllegalArgumentException {
		for (DurationFieldType durationFieldType : unsupportedFieldsForDurationBasedFormatting) {
			if (periodType.indexOf(durationFieldType) != -1) {
				throw new IllegalArgumentException(durationFieldType + " is not supported for this type of formatting.");
			}
		}
	}
    
    private static Period periodGenerate(Duration duration, PeriodType periodType, RoundingMode roundingMode, YukonUserContext yukonUserContext) {
    	
    	Duration adjustedDuration = roundDuration(duration, periodType, roundingMode, yukonUserContext);
        Period result = new Period(adjustedDuration, periodType, getChronologyForUser(yukonUserContext)).normalizedStandard(periodType);
        return result;
    }
    
    private static Period periodGenerate(ReadableInstant startTime, Duration duration, PeriodType periodType, RoundingMode roundingMode, YukonUserContext yukonUserContext) {
        // Note that this method essentially rounds the endTime while fixing in time the startTime. This is
        // somewhat of an arbitrary choice (it could work the other way).
    	
        Duration adjustedDuration = roundDuration(duration,
                                                   periodType,
                                                   roundingMode,
                                                   yukonUserContext);
        
        DateTime startWithFixedChronology = new DateTime(startTime, yukonUserContext.getJodaTimeZone());
        Period result = new Period(startWithFixedChronology, adjustedDuration, periodType);
        return result;
    }

    /**
     * Will round a Duration according to the last field of the PeriodType and the
     * indicated RoundingMode. 
     * 
     * The duration is rounded by dividing it by the number of milliseconds in the
     * last field of the PeriodType. For example, the last field of day-hour type
     * is hour. There are 3,600,000ms in an hour. So, rounding a duration
     * of 1 hour 10 minutes would look like:
     * 
     * 4,200,000 (ms)/ 3,600,000 (ms/hour) = 1 1/6 (hours)
     * 
     * This value then be rounded according to the rounding mode:
     * 
     * HALF_UP would give 1 (hour)
     * FLOOR would give 1 (hour)
     * CEILING would give 2 (hour)
     * 
     * This rounded value is then converted back into a millisecond based Duration and returned.
     * 
     * @return
     */
    private static Duration roundDuration(Duration duration, PeriodType periodType,
            RoundingMode roundingMode, YukonUserContext yukonUserContext) {
        DurationFieldType lastFieldType = periodType.getFieldType(periodType.size() - 1);
        ISOChronology chrono = ISOChronology.getInstance(yukonUserContext.getJodaTimeZone());
        DurationField lastField = lastFieldType.getField(chrono);
        long lastUnitMillis = lastField.getUnitMillis();
        
        BigDecimal bigDuration = new BigDecimal(duration.getMillis());
        BigDecimal bigMillis = new BigDecimal(lastUnitMillis);
        
        BigDecimal remainder = bigDuration.remainder(bigMillis);
        BigDecimal adjustment = remainder.divide(bigMillis, 0, roundingMode);
        long milliAdjustment = adjustment.longValueExact() * lastUnitMillis;
        
        Duration adjustedDuration = duration.plus(milliAdjustment);
        return adjustedDuration;
    }

    
    private static Chronology getChronologyForUser(YukonUserContext yukonUserContext) {
    	return ISOChronology.getInstance(yukonUserContext.getJodaTimeZone());
    }
    
    public void setUnsupportedFieldsForDurationBasedFormatting(Set<DurationFieldType> unsupportedFieldsForDurationBasedFormatting) {
		this.unsupportedFieldsForDurationBasedFormatting = unsupportedFieldsForDurationBasedFormatting;
	}
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

}
