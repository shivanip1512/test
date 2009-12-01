package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class DateFormattingServiceImpl implements DateFormattingService {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private SystemDateFormattingService systemDateFormattingService;

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setSystemDateFormattingService(
            SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }
    
    private Map<String, FlexibleDateParser> dateParserLookup = new HashMap<String, FlexibleDateParser>();

    public String format(Object object, DateFormatEnum type, YukonUserContext userContext)
            throws IllegalArgumentException {
        Validate.notNull(object, "Date object is null in DateFormattingServiceImpl.formatDate()");
        if (object instanceof Date) {

            Date date = (Date) object;
            DateFormat df = getDateFormatter(type, userContext);

            // will result in dates that would normally format to midnight of a
            // date, to format instead to the previous date.
            // MidnightMode.INCLUDES_MIDNIGHT is only set on date-only type
            // DateFormatEnum values
            if (type.getMidnightMode() == MidnightMode.INCLUDES_MIDNIGHT) {
                date = DateUtils.addMilliseconds(date, -1);
            }

            return df.format(date);
        } else if (object instanceof ReadablePartial) {
            ReadablePartial partial = (ReadablePartial) object;

            DateTimeFormatter formatter = getDateTimeFormatter(type,
                                                               userContext);
            return formatter.print(partial);

        } else if (object instanceof ReadableInstant) {
            ReadableInstant instant = (ReadableInstant) object;

            DateTimeFormatter formatter = getDateTimeFormatter(type,
                                                               userContext);
            return formatter.print(instant);
        } else {
            throw new IllegalArgumentException("Date object is not supported in DateFormattingServiceImpl.formatDate()");
        }
    }

    public DateFormat getDateFormatter(DateFormatEnum type, YukonUserContext userContext)
            throws IllegalArgumentException {

        String format = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(type.getFormatKey());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, userContext.getLocale());
        simpleDateFormat.setTimeZone(userContext.getTimeZone());
        
        return simpleDateFormat;

    }
    
    @Override
    public DateTimeFormatter getDateTimeFormatter(DateFormatEnum type, YukonUserContext userContext) {

    	String format = 
    		messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(type.getFormatKey());
    	DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
    	DateTimeZone dateTimeZone = userContext.getJodaTimeZone();
    	formatter.withZone(dateTimeZone);
        
        return formatter;
    }

    public synchronized Date flexibleDateParser(String dateStr,
            DateOnlyMode mode, YukonUserContext userContext) throws ParseException {

        String parserName = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.common.dateFormatting.parserImplementation");
        FlexibleDateParser flexibleDateParser = dateParserLookup.get(parserName);
        Date result = flexibleDateParser.parseDate(dateStr, mode, userContext.getLocale(), userContext.getTimeZone());
        return result;
    }

    public Date flexibleDateParser(String dateStr, YukonUserContext userContext)
            throws ParseException {
        return flexibleDateParser(dateStr, DateOnlyMode.START_OF_DAY, userContext);
    }

    public synchronized Date flexibleDateParserWithSystemTimeZone(String dateStr,
            DateOnlyMode mode, YukonUserContext userContext) throws ParseException {

        String parserName = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.common.dateFormatting.parserImplementation");
        FlexibleDateParser flexibleDateParser = dateParserLookup.get(parserName);
        Date result = flexibleDateParser.parseDate(dateStr, mode, userContext.getLocale(), systemDateFormattingService.getSystemTimeZone());
        return result;
    }

    public LocalTime parseLocalTime(String localTimeStr,
            YukonUserContext userContext) throws ParseException {
        String parserName = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.common.dateFormatting.parserImplementation");
        FlexibleDateParser flexibleDateParser = dateParserLookup.get(parserName);
        LocalTime result = flexibleDateParser.parseTimeAsLocalTime(localTimeStr,
                                                                   userContext.getLocale(),
                                                                   userContext.getTimeZone());
        return result;
    }
    
    public Calendar getCalendar(YukonUserContext userContext) {
        return Calendar.getInstance(userContext.getTimeZone(), userContext.getLocale());
    }
    
    public void setDateParserLookup(Map<String, FlexibleDateParser> dateParserLookup) {
        this.dateParserLookup = dateParserLookup;
    }

    @Override
    public String formatPeriod(Object periodObj, PeriodFormatEnum type,
            YukonUserContext userContext) throws IllegalArgumentException {
        Validate.notNull(periodObj, "period object is null in DateFormattingServiceImpl.formatDate()");
        if (periodObj instanceof ReadablePeriod) {
            ReadablePeriod period = (ReadablePeriod) periodObj;

            PeriodFormatter formatter = getPeriodFormatter(type, userContext);
            return formatter.print(period);
        } else if (periodObj instanceof ReadableDuration) {
            ReadablePeriod period = new Period(periodObj, PeriodType.time());

            PeriodFormatter formatter = getPeriodFormatter(type, userContext);
            return formatter.print(period);
        } else {
            throw new IllegalArgumentException("object is not supported in DateFormattingServiceImpl.formatPeriod()");
        }
    }

    @Override
    public PeriodFormatter getPeriodFormatter(PeriodFormatEnum type,
            YukonUserContext userContext) {

        Validate.notNull(type, "a valid type must be specified");

        // Currently Joda doesn't support formatting configurable with a string
        // for periods so for now, we only support "h:mm" for periods and
        // durations.
        PeriodFormatterBuilder formatBuilder = new PeriodFormatterBuilder();
        formatBuilder.printZeroAlways()
                     .appendHours()
                     .appendSuffix(":")
                     .minimumPrintedDigits(2)
                     .appendMinutes();
        return formatBuilder.toFormatter();
    }
}
