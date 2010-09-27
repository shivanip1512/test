package com.cannontech.common.util;

import java.util.Locale;

import org.joda.time.DurationFieldType;
import org.joda.time.PeriodType;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;

public class SimplePeriodFormat {
    private static final PeriodType configPeriodType = PeriodType.standard().withYearsRemoved().withMonthsRemoved();
    private static final PeriodFormatter configPeriodFormatter;

    static {
        PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
        builder.printZeroRarelyLast();
        builder.appendWeeks().appendSuffix("w");
        builder.appendDays().appendSuffix("d");
        builder.appendHours().appendSuffix("h");
        builder.appendMinutes().appendSuffix("m");
        builder.appendSecondsWithOptionalMillis().appendSuffix("s");
        configPeriodFormatter = builder.toFormatter();
    }
    
    public static PeriodType getConfigPeriodFormatterPeriodType() {
        return configPeriodType;
    }
    
    /**
     * Creates a period formatter that follows a common, informal short hand for 
     * representing periods.
     * 
     * <pre>
     * Fully specified (where X is an integer and YYY is an integer between 0 and 999 inclusive):
     * XwXdXhXmX.YYYS
     * 
     * Supported letters:
     * w -- week
     * d -- day
     * h -- hour
     * m -- minute
     * s -- second
     * 
     * These "field types" were chosen because they have easily definable lengths. We want the Periods
     * returned by this formatter to be easily converted into a "standard" Duration.
     * 
     * Individual parts can be dropped to form simple expressions:
     * 1 week: 1w
     * 1.5 days: 1d12h
     * 1 hour and 15 minutes: 1h15m
     * 750 millisconds: .750s
     * </pre>
     * 
     * @return
     */
    public static PeriodFormatter getConfigPeriodFormatter() {
        return configPeriodFormatter;
    }
    
    /**
     * Works similarly to {@link #getConfigPeriodFormatter()} but allows a fallback field to be specified
     * for cases where the input String contains only numbers (i.e. can be parsed with {@link Integer#parseInt(String)} 
     * without throwing an Exception).
     * @param duationFieldType the field type to use for fallback cases
     * @return
     */
    public static PeriodFormatter getConfigPeriodFormatterWithFallback(final DurationFieldType duationFieldType) {
        PeriodParser specialParser = new PeriodParser() {
            
            @Override
            public int parseInto(ReadWritablePeriod period, String periodStr, int position, Locale locale) {
                // special parser implementation to handle numbers with no letters
                try {
                    int parseInt = Integer.parseInt(periodStr.substring(position));
                    period.set(duationFieldType, parseInt);
                    return periodStr.length();
                } catch (NumberFormatException e) {
                    // we'll assume it can be parsed with the internalFormatter
                }
                
                int returnValue = configPeriodFormatter.getParser().parseInto(period, periodStr, position, locale);
                return returnValue;

            }
        };
        
        return new PeriodFormatter(configPeriodFormatter.getPrinter(), specialParser);
    }
    
}
