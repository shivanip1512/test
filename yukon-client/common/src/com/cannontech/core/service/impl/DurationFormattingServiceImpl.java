package com.cannontech.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Chronology;
import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
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
    
    private static Pattern symbolPattern = Pattern.compile("^(.*?)(%(?:[YDMHS]|MO)(?:_ABBR|_FULL)?%)(.*)$");
    
    // FORMAT DURATION - DEFAULT ROUNDING
    public String formatDuration(long durationValue, TimeUnit durationUnit, DurationFormat type, YukonUserContext yukonUserContext) {
    	return formatDuration(durationValue, durationUnit, type, type.getRoundRightmostUpDefault(), yukonUserContext);
    }
    
    // FORMAT DURATION - ROUNDING PARAM
    public String formatDuration(final long durationValue, final TimeUnit durationUnit, DurationFormat type, boolean roundRightmostUp, YukonUserContext yukonUserContext) {
    	
    	PeriodGenerator periodGenerator = new PeriodGenerator() {
			@Override
			public Period generatePeriod(PeriodType periodType) {
				long durationMillis = TimeUnit.MILLISECONDS.convert(durationValue, durationUnit);
		    	Period period = new Period(durationMillis, periodType);
		    	return period;
			}
		};
    	
    	FormattedDurationTemplate template = new FormattedDurationTemplate(periodGenerator);
    	return template.getFormattedDuration(type, roundRightmostUp, yukonUserContext);
    }
    
    // FORMAT DURATION - START/END, DEFAULT ROUNDING
    @Override
	public String formatDuration(Date startDate, Date endDate, DurationFormat type, YukonUserContext yukonUserContext) {
    	return formatDuration(startDate, endDate, type, type.getRoundRightmostUpDefault(), yukonUserContext);
	}
    
    // FORMAT DURATION - START/END, ROUNDING PARAM
    @Override
	public String formatDuration(final Date startDate, final Date endDate, DurationFormat type, boolean roundRightmostUp, final YukonUserContext yukonUserContext) {
		
    	PeriodGenerator periodGenerator = new PeriodGenerator() {
			@Override
			public Period generatePeriod(PeriodType periodType) {
				Chronology chronology = ISOChronology.getInstance(yukonUserContext.getJodaTimeZone());
		    	Period period = new Period(startDate.getTime(), endDate.getTime(), periodType, chronology);
		    	return period;
			}
		};
    	
    	FormattedDurationTemplate template = new FormattedDurationTemplate(periodGenerator);
    	return template.getFormattedDuration(type, roundRightmostUp, yukonUserContext);
	}

    // FORMAT DURATION - START/END, DEFAULT ROUNDING
    @Override
    public String formatDuration(ReadableInstant startDate, ReadableInstant endDate, 
                                  DurationFormat type, YukonUserContext yukonUserContext) {
        return formatDuration(startDate, endDate, type, type.getRoundRightmostUpDefault(), 
                               yukonUserContext);
    }
    
    // FORMAT DURATION - START/END, ROUNDING PARAM
    @Override
    public String formatDuration(final ReadableInstant startDate, 
                                  final ReadableInstant endDate, 
                                  DurationFormat type, 
                                  boolean roundRightmostUp,
                                  final YukonUserContext yukonUserContext) {
        
        PeriodGenerator periodGenerator = new PeriodGenerator() {
            @Override
            public Period generatePeriod(PeriodType periodType) {
                Chronology chronology = ISOChronology.getInstance(yukonUserContext.getJodaTimeZone());
                ReadableDuration duration = new Duration(startDate, endDate);
                Period period = new Period(duration, periodType, chronology);
                return period;
            }
        };
        
        FormattedDurationTemplate template = new FormattedDurationTemplate(periodGenerator);
        return template.getFormattedDuration(type, roundRightmostUp, yukonUserContext);
    }

    
    // TEMPLATE
    private class FormattedDurationTemplate {
    	
    	private PeriodGenerator periodGenerator;
    	
    	public FormattedDurationTemplate(PeriodGenerator periodGenerator) {
    		this.periodGenerator = periodGenerator;
    	}
    	
    	public String getFormattedDuration(DurationFormat type, boolean roundRightmostUp, YukonUserContext yukonUserContext) {
    		
    		List<String> parsedFormat = getParsedFormat(type, yukonUserContext);
        	List<DurationFieldType> durationFieldTypeList = getDurationFieldTypeList(parsedFormat);
        	PeriodType periodType = createPeriodType(durationFieldTypeList, roundRightmostUp);
        	PeriodFormatter periodFormatter = createPeriodFormatter(parsedFormat, yukonUserContext);
        	
        	Period period = periodGenerator.generatePeriod(periodType);
        	
        	return getFormattedString(periodFormatter, period, durationFieldTypeList, roundRightmostUp);
    	}
    }
    
    private interface PeriodGenerator {
    	public Period generatePeriod(PeriodType periodType);
    }
    
    
    // HELPERS
    /**
     * Does final cleanup work on the period and uses the formatter to output as a string.
     */
    private String getFormattedString(PeriodFormatter periodFormatter, Period period, List<DurationFieldType> durationFieldTypeList, boolean roundRightmostUp) {
    	
    	if (roundRightmostUp) {
    		period = roundPeriod(period, durationFieldTypeList);
    	}
    	
    	period = period.normalizedStandard(period.getPeriodType());
    	
    	// format
    	StringBuffer out = new StringBuffer();
    	periodFormatter.printTo(out, period);
    	return out.toString();
    }
    
    /**
     * Adjusts the period to round up rightmost duration field if the period also support the field to it immediate left.
     */
    private Period roundPeriod(Period period, List<DurationFieldType> durationFieldTypeList) {
    	
    	// milliseconds to seconds
		if (durationFieldTypeList.contains(DurationFieldType.millis()) && durationFieldTypeList.contains(DurationFieldType.seconds())) {
			if (period.getMillis() >= 500) {
				period = period.minusMillis(period.getMillis());
				period = period.plusSeconds(1);
			}
		}
		// seconds to minutes
		else if (durationFieldTypeList.contains(DurationFieldType.seconds()) && durationFieldTypeList.contains(DurationFieldType.minutes())) {
			if (period.getSeconds() >= 30) {
				period = period.minusSeconds(period.getSeconds());
				period = period.plusMinutes(1);
			}
		}
		// minutes to hours
		else if (durationFieldTypeList.contains(DurationFieldType.minutes()) && durationFieldTypeList.contains(DurationFieldType.hours())) {
			if (period.getMinutes() >= 30) {
				period = period.minusMinutes(period.getMinutes());
    			period = period.plusHours(1);
			}
		}
		
		return period;
    }
    
    /**
     * Creates PeriodFormatter by using a PeriodFormatterBuilder and the "instructions" contained in the parsed format.
     */
    private PeriodFormatter createPeriodFormatter(List<String> parsedFormat, YukonUserContext yukonUserContext) {
    
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
    	
	    PeriodFormatterBuilder periodFormatterBuilder = new PeriodFormatterBuilder();
		periodFormatterBuilder.printZeroAlways();
	    
		// builder loop
		for (String subFormat : parsedFormat) {
			
			if (StringUtils.isEmpty(subFormat)) {
				continue;
			}
			
			// process symbol
			if (isDurationFormatSymbolSubstring(subFormat)) {
				
				DurationFormatSymbol symbol = DurationFormatSymbol.valueOf(String.valueOf(subFormat.substring(1, subFormat.length() - 1)));
				
				String singularSuffix = messageSourceAccessor.getMessage(symbol.getSingularSuffixKey());
				String pluralSuffix = messageSourceAccessor.getMessage(symbol.getPluralSuffixKey());
				symbol.applyAppenderToBuilder(periodFormatterBuilder, singularSuffix, pluralSuffix);
			
			// append plain text
			} else {
				periodFormatterBuilder.appendLiteral(subFormat);
			}
		}
		
		PeriodFormatter formatter = periodFormatterBuilder.toFormatter().withLocale(yukonUserContext.getLocale());
		return formatter;
    }
    
    /**
     * Gets the format string for the DurationFormat and parses it into an intermediate "instructions" list.
     */
    private List<String> getParsedFormat(DurationFormat type, YukonUserContext yukonUserContext) {
    	
    	MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        String format = messageSourceAccessor.getMessage(type); 
        
    	List<String> subFormats = Lists.newArrayList();
    	return parseFormat(subFormats, format);
    }
    
    /**
     * Creates a Period type that only contains those duration fields that the format requires. 
     * An additional field may be added to the right to maintain precision needed for rounding.
     */
    private PeriodType createPeriodType(List<DurationFieldType> durationFieldTypeList, boolean roundRightmostUp) {
    	
    	// round - create a duration field to the right of the rightmost field. 
    	// This is required to create room for the extra millis that would otherwise be truncated from Period. 
    	// We want to preserve these millis to determine if we have enough to round up.
    	if (roundRightmostUp) {
    		
    		if (durationFieldTypeList.contains(DurationFieldType.seconds())) {
    			durationFieldTypeList.add(DurationFieldType.millis());
    		} else if (durationFieldTypeList.contains(DurationFieldType.minutes())) {
    			durationFieldTypeList.add(DurationFieldType.seconds());
    		} else if (durationFieldTypeList.contains(DurationFieldType.hours())) {
    			durationFieldTypeList.add(DurationFieldType.minutes());
    		}
    	}
    	
    	// period - only create a Period that supports the DurationFieldTypes we intend to display in the formated string
    	// Using a Period with all the standard fields will cause the millis to get dispersed from left to right and we may lose them if those left side fields are not used in the format.
    	PeriodType periodType = PeriodType.forFields(durationFieldTypeList.toArray(new DurationFieldType[durationFieldTypeList.size()]));
    	
    	return periodType;
    }
    
    /**
     * Loops over the parsed format instructions and determines which DurationFieldTypes the Period will need to support.
     */
    private List<DurationFieldType> getDurationFieldTypeList(List<String> parsedFormat) {
    	
    	List<DurationFieldType> durationFieldTypesList = Lists.newArrayList();
    	for (String subFormat : parsedFormat) {
    		if (isDurationFormatSymbolSubstring(subFormat)) {
    			DurationFormatSymbol symbol = DurationFormatSymbol.valueOf(String.valueOf(subFormat.substring(1, subFormat.length() - 1)));
    			durationFieldTypesList.add(symbol.getDurationFieldType());
    		}
    	}
    	return durationFieldTypesList;
    }
    
    /**
     * Recursively applies a grouping regular expression to break the format string into format tokens and literals.
     */
    private static List<String> parseFormat(List<String> partsList, String remainder) {
		Matcher m = symbolPattern.matcher(remainder);
		if (m.matches()) {
			partsList.add(m.group(1));
			partsList.add(m.group(2));
			partsList = parseFormat(partsList, m.group(3));
		} else {
			partsList.add(remainder);
		}
		return partsList;
	}
    
    private boolean isDurationFormatSymbolSubstring(String s) {
    	if (StringUtils.isNotBlank(s) && s.charAt(0) == '%' && s.charAt(s.length() - 1) == '%') {
			return true;
    	}
    	return false;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

}
