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

import org.apache.commons.lang.StringUtils;

import com.cannontech.core.service.DateFormattingService.DateOnlyMode;

public class ConfigurableFlexibleDateParser implements FlexibleDateParser {

    private List<String[]> alternativeAmPmList = new ArrayList<String[]>(2);
    private List<String> dateTimeFormats = new ArrayList<String>();
    private List<String> dateFormats = new ArrayList<String>();

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
                    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
                    if (alternativeAmPm.length == 2) {
                        dateFormatSymbols.setAmPmStrings(alternativeAmPm);
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, dateFormatSymbols);
                    simpleDateFormat.setTimeZone(timeZone);
                    Date result = simpleDateFormat.parse(dateStr);
                    return result;
                } catch (ParseException e) {
                    // do nothing, try next pattern
                }
            }

        }
        throw new ParseException(dateStr, 0);
    }

    public void setDateFormats(List<String> dateFormats) {
        this.dateFormats = dateFormats;
    }
    
    public void setDateTimeFormats(List<String> dateTimeFormats) {
        this.dateTimeFormats = dateTimeFormats;
    }
    
    public void setAlternativeAmPmList(List<String[]> alternativeAmPmList) {
        this.alternativeAmPmList = alternativeAmPmList;
    }
}
