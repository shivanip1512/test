package com.cannontech.common.util;

import java.util.Locale;

import org.joda.time.DurationFieldType;
import org.joda.time.MutablePeriod;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;

public class SimplePeriodFormat {
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
    
    public static PeriodFormatter getConfigPeriodFormatter() {
        return configPeriodFormatter;
    }
    
    public static PeriodFormatter getConfigPeriodFormatterWithFallback(final DurationFieldType duationFieldType) {
        PeriodParser specialParser = new PeriodParser() {
            
            @Override
            public int parseInto(ReadWritablePeriod period, String periodStr, int position, Locale locale) {
                // special parser implementation to handle numbers with no letters
                try {
                    int parseInt = Integer.parseInt(periodStr);
                    period.set(duationFieldType, parseInt);
                    return periodStr.length();
                } catch (NumberFormatException e) {
                    // we'll assume it can be parsed with the internalFormatter
                }
                
                ReadWritablePeriod tempPeriod = new MutablePeriod();
                int returnValue = configPeriodFormatter.getParser().parseInto(tempPeriod, periodStr, position, locale);
                period.setPeriod(tempPeriod);
                return returnValue;

            }
        };
        
        return new PeriodFormatter(configPeriodFormatter.getPrinter(), specialParser);
    }
    
}
