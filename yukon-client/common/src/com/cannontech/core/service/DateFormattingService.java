package com.cannontech.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.cannontech.user.YukonUserContext;

/**
 * This is the primary means within Yukon for formatting and parsing date
 * for the user interface.
 * 
 * Please see the com/cannontech/yukon/common/dateFormatting.xml file
 * for the actual format strings.
 * 
 * @author tmack
 *
 */
public interface DateFormattingService {

    static public enum DateFormatEnum {
        TIME, 
        TIME_TZ,
        DATE, 
        BOTH, 
        DATEHM, 
        DATEH_AP,
        ;

        private final static String keyPrefix = "yukon.common.dateFormatting.";

        public String getFormatKey() {
            return keyPrefix + name();
        }

    }

    public static enum DateOnlyMode {
        START_OF_DAY, END_OF_DAY
    };

    /**
     * Convert a date to a string using the userContext for locale and timezone information.
     * The type dictates a suggested style, but not a specific format.
     * 
     * @param date
     * @param type
     * @param userContext
     * @return
     */
    public String formatDate(Date date, DateFormatEnum type, YukonUserContext userContext);

    /**
     * This method is provided for completeness, but shouldn't not be used for regular code.
     * @param type
     * @param userContext
     * @return
     */
    public DateFormat getDateFormatter(DateFormatEnum type, YukonUserContext userContext);

    public Date flexibleDateParser(String dateStr, DateOnlyMode mode,
                                   YukonUserContext userContext) throws ParseException;

    public Date flexibleDateParser(String dateStr, YukonUserContext userContext)
            throws ParseException;

    public Calendar getCalendar(YukonUserContext userContext);

}
