package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
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

    public String formatDate(Date date, DateFormatEnum type, YukonUserContext userContext)
            throws IllegalArgumentException {
        DateFormat df = getDateFormatter(type, userContext);
        if (date != null) {
            
            // will result in dates that would normally format to midnight of a date, to format instead
            // to the previous date.
            // MidnightMode.INCLUDES_MIDNIGHT is only set on date-only type DateFormatEnum values
            if (type.getMidnightMode() == MidnightMode.INCLUDES_MIDNIGHT) {
                date = DateUtils.addMilliseconds(date, -1);
            }
            
            return df.format(date);
        } else {
            throw new IllegalArgumentException("Date object is null in DateFormattingServiceImpl.formatDate()");
        }
    }

    public DateFormat getDateFormatter(DateFormatEnum type, YukonUserContext userContext)
            throws IllegalArgumentException {

        String format = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(type.getFormatKey());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, userContext.getLocale());
        simpleDateFormat.setTimeZone(userContext.getTimeZone());
        
        return simpleDateFormat;

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
    
    public Calendar getCalendar(YukonUserContext userContext) {
        return Calendar.getInstance(userContext.getTimeZone(), userContext.getLocale());
    }
    
    public void setDateParserLookup(Map<String, FlexibleDateParser> dateParserLookup) {
        this.dateParserLookup = dateParserLookup;
    }

}
