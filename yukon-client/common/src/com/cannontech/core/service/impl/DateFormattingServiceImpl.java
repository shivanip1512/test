package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DateFormattingServiceImpl implements DateFormattingService {

    private YukonUserDao yukonUserDao;

    private DateFormatSymbols apSymbols;
    {
        apSymbols = new DateFormatSymbols();
        apSymbols.setAmPmStrings(new String[] { "A", "P" });
    }

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

    public String formatDate(Date date, DateFormatEnum type, LiteYukonUser user)
            throws IllegalArgumentException {
        final TimeZone zone = yukonUserDao.getUserTimeZone(user);
    
        DateFormat df = getDateFormatter(type, user);
        df.setTimeZone(zone);
        if (date != null) {
            return df.format(date);
        } else {
            throw new IllegalArgumentException("Date object is null in DateFormattingServiceImpl.formatDate()");
        }
    }

    public DateFormat getDateFormatter(DateFormatEnum type, LiteYukonUser user)
            throws IllegalArgumentException {

        return new SimpleDateFormat(type.getFormat());

    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public synchronized Date flexibleDateParser(String dateStr,
            DateOnlyMode mode, LiteYukonUser user) throws ParseException {

        TimeZone timeZone = yukonUserDao.getUserTimeZone(user);

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

    public Date flexibleDateParser(String dateStr, LiteYukonUser user)
            throws ParseException {
        return flexibleDateParser(dateStr, DateOnlyMode.START_OF_DAY, user);
    }

    public Calendar getCalendar(LiteYukonUser user) {
        return Calendar.getInstance(yukonUserDao.getUserTimeZone(user));
    }

}
