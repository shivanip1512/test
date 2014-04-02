package com.cannontech.core.service.impl;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.cannontech.core.service.DateFormattingService.DateOnlyMode;

public class ConfigurableFlexibleDateParser implements FlexibleDateParser {

    private List<String[]> alternativeAmPmList = new ArrayList<String[]>(2);
    private List<String> dateTimeFormats = new ArrayList<String>();
    private List<String> dateFormats = new ArrayList<String>();
    private List<String> timeFormats = new ArrayList<String>();

    @Override
    public Date parseDate(String dateStr, DateOnlyMode mode, Locale locale, TimeZone timeZone)
        throws ParseException {
        try {
            return parseDateTime(dateStr, locale, timeZone);
        } catch (ParseException e) {
        }
        
        for (String pattern : dateFormats) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
                simpleDateFormat.setTimeZone(timeZone);
                simpleDateFormat.setLenient(false);
                Date result = simpleDateFormat.parse(dateStr);
                if (mode == DateOnlyMode.END_OF_DAY) {
                    Calendar calendar = simpleDateFormat.getCalendar();
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    result = calendar.getTime();
                }
                return result;
            } catch (ParseException e) {
                // do nothing, try next pattern
            }
        }
        throw new ParseException(dateStr, 0);
    }

    @Override
    public Date parseDateTime(String dateStr, Locale locale, TimeZone timeZone) throws ParseException {
        if (StringUtils.isBlank(dateStr)) return null;

        for (String pattern : dateTimeFormats) {
            for (String[] alternativeAmPm : alternativeAmPmList) {
                try {
                    DateFormatSymbols dateFormatSymbols = getDateFormatSymbolsWithAlternateAmPm(alternativeAmPm, locale);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, dateFormatSymbols);
                    simpleDateFormat.setTimeZone(timeZone);
                    simpleDateFormat.setLenient(false);
                    Date result = simpleDateFormat.parse(dateStr);
                    return result;
                } catch (ParseException e) {
                    // do nothing, try next pattern
                }
            }

        }
        throw new ParseException(dateStr, 0);
    }

    @Override
    public LocalTime parseTimeAsLocalTime(String timeStr, 
                                           Locale locale,
                                           TimeZone timeZone) throws ParseException {
        for (String pattern : timeFormats) {
            for (String[] alternativeAmPm : alternativeAmPmList) {
                DateFormatSymbols dateFormatSymbols = getDateFormatSymbolsWithAlternateAmPm(alternativeAmPm, locale);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, dateFormatSymbols);
                simpleDateFormat.setTimeZone(timeZone);
                simpleDateFormat.setLenient(false);
                Date result = null;
                try {
                    result = simpleDateFormat.parse(timeStr);
                    
                } catch (ParseException pe) {
                    // try next pattern
                    continue;
                }
                DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
                return new LocalTime(result, dateTimeZone);
            }
        }
        throw new ParseException(timeStr, 0);
    }


    @Override
    public LocalDate parseDateAsLocalDate(String dateStr, 
                                           Locale locale,
                                           TimeZone timeZone) throws ParseException {
        for (String pattern : dateFormats) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
            simpleDateFormat.setTimeZone(timeZone);
            simpleDateFormat.setLenient(false);
            Date result = null;
            try {
                result = simpleDateFormat.parse(dateStr);
                    
            } catch (ParseException pe) {
                // try next pattern
                continue;
            }
            DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
            return new LocalDate(result, dateTimeZone);
        }
        throw new ParseException(dateStr, 0);
    }
    
    private DateFormatSymbols getDateFormatSymbolsWithAlternateAmPm(String[] alternativeAmPm, Locale locale) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        if (alternativeAmPm.length == 2) {
            dateFormatSymbols.setAmPmStrings(alternativeAmPm);
        }
        return dateFormatSymbols;
    }

    public void setDateFormats(List<String> dateFormats) {
        this.dateFormats = dateFormats;
    }

    public void setDateTimeFormats(List<String> dateTimeFormats) {
        this.dateTimeFormats = dateTimeFormats;
    }

    public void setTimeFormats(List<String> timeFormats) {
        this.timeFormats = timeFormats;
    }

    public void setAlternativeAmPmList(List<String[]> alternativeAmPmList) {
        this.alternativeAmPmList = alternativeAmPmList;
    }

}
