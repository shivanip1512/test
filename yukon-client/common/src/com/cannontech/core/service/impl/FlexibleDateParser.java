package com.cannontech.core.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.cannontech.core.service.DateFormattingService.DateOnlyMode;

public interface FlexibleDateParser {
    
    public Date parseDate(String dateStr,
                          DateOnlyMode mode, 
                          Locale locale,
                          TimeZone timeZone) throws ParseException;
    
    public Date parseDateTime(String dateStr,
                              Locale locale,
                              TimeZone timeZone) throws ParseException;

    public LocalTime parseTimeAsLocalTime(String timeStr, 
                                           Locale locale,
                                           TimeZone timeZone) throws ParseException;
    
    
    public LocalDate parseDateAsLocalDate(String dateStr, 
                                           Locale locale,
                                           TimeZone timeZone) throws ParseException;
                                  
}
