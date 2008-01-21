package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class DateFormattingServiceImpl implements DateFormattingService {
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    private DateFormatSymbols apSymbols;
    {
        apSymbols = new DateFormatSymbols();
        apSymbols.setAmPmStrings(new String[] { "A", "P" });
    }

    //i18n
    private DateFormat dateTimeFormat[] = {
            new SimpleDateFormat("MM/dd/yy hh:mma", apSymbols),
            new SimpleDateFormat("MM/dd/yyyy hh:mma", apSymbols),
            new SimpleDateFormat("MM/dd/yy hh:mma"),
            new SimpleDateFormat("MM/dd/yyyy hh:mma"),
            new SimpleDateFormat("MM/dd/yy hh:mm a", apSymbols),
            new SimpleDateFormat("MM/dd/yyyy hh:mm a", apSymbols),
            new SimpleDateFormat("MM/dd/yy hh:mm a"),
            new SimpleDateFormat("MM/dd/yyyy hh:mm a"),
            new SimpleDateFormat("MM/dd/yy HH:mm"),
            new SimpleDateFormat("MM/dd/yyyy HH:mm"), };

    private DateFormat dateFormat[] = { new SimpleDateFormat("MM/dd/yy"),
            new SimpleDateFormat("MM/dd/yyyy"), };

    public String formatDate(Date date, DateFormatEnum type, YukonUserContext userContext)
            throws IllegalArgumentException {
        final TimeZone zone = userContext.getTimeZone();
    
        DateFormat df = getDateFormatter(type, userContext);
        df.setTimeZone(zone);
        if (date != null) {
            return df.format(date);
        } else {
            throw new IllegalArgumentException("Date object is null in DateFormattingServiceImpl.formatDate()");
        }
    }

    public DateFormat getDateFormatter(DateFormatEnum type, YukonUserContext userContext)
            throws IllegalArgumentException {

        String format = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(type.getFormatKey());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, userContext.getLocale());
        
        return simpleDateFormat;

    }

    public synchronized Date flexibleDateParser(String dateStr,
            DateOnlyMode mode, YukonUserContext userContext) throws ParseException {

        TimeZone timeZone = userContext.getTimeZone();

        if (StringUtils.isBlank(dateStr))
            return null;
        for (DateFormat format : dateTimeFormat) {
            try {
                format.setTimeZone(timeZone);
                Date date = format.parse(dateStr);
                return date;
            } catch (ParseException e) {}
        }
        for (DateFormat format : dateFormat) {
            try {
                format.setTimeZone(timeZone);
                Date date = format.parse(dateStr);
                if (mode == DateOnlyMode.END_OF_DAY) {
                    return DateUtils.addDays(date, 1);
                }
                return date;
            } catch (ParseException e) {}
        }
        throw new ParseException(dateStr, 0);
    }

    public Date flexibleDateParser(String dateStr, YukonUserContext userContext)
            throws ParseException {
        return flexibleDateParser(dateStr, DateOnlyMode.START_OF_DAY, userContext);
    }

    public Calendar getCalendar(YukonUserContext userContext) {
        return Calendar.getInstance(userContext.getTimeZone());
    }

}
