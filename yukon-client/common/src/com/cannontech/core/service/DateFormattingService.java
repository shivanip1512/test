package com.cannontech.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface DateFormattingService {

    static public enum DateFormatEnum {
        TIME("HH:mm"), DATE("MM/dd/yyyy"), BOTH("MM/dd/yyyy HH:mm:ss z"), DATEHM("MM/dd/yyyy HH:mm z");

        private final String format;

        private DateFormatEnum(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    public static enum DateOnlyMode {
        START_OF_DAY, END_OF_DAY
    };

    public String formatDate(Date date, DateFormatEnum type, LiteYukonUser user);

    public DateFormat getDateFormatter(DateFormatEnum type, LiteYukonUser user);

    public Date flexibleDateParser(String dateStr, DateOnlyMode mode,
            LiteYukonUser user) throws ParseException;

    public Date flexibleDateParser(String dateStr, LiteYukonUser user)
            throws ParseException;

    public GregorianCalendar getGregorianCalendar(LiteYukonUser user);

}
