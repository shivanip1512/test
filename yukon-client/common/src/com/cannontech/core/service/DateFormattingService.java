package com.cannontech.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;

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
        TIME(MidnightMode.NORMAL), 
        TIME24H(MidnightMode.NORMAL), 
        TIME_TZ(MidnightMode.NORMAL),
        TIMEZONE(MidnightMode.NORMAL),
        TIMEZONE_EXTENDED(MidnightMode.NORMAL),
        DATE(MidnightMode.NORMAL), 
        BOTH(MidnightMode.NORMAL), 
        FULL(MidnightMode.NORMAL), 
        DATEHM(MidnightMode.NORMAL), 
        DATEHM_12(MidnightMode.NORMAL), 
        DATEHMS_12(MidnightMode.NORMAL), 
        DATEH(MidnightMode.NORMAL),
        DATE_YYYYMMdd(MidnightMode.NORMAL),
        HOUR(MidnightMode.NORMAL),
        DATE_MIDNIGHT_PREV(MidnightMode.INCLUDES_MIDNIGHT),
        LONG_DATE(MidnightMode.NORMAL),
        LONG_DATE_TIME(MidnightMode.NORMAL),
        VERY_SHORT(MidnightMode.NORMAL),
        SHORT_MONTH_YEAR(MidnightMode.NORMAL),
        MONTH_YEAR(MidnightMode.NORMAL),
        MONTH_DAY_HM(MidnightMode.NORMAL),
        FILE_TIMESTAMP(MidnightMode.NORMAL),
        ;
        
        private final MidnightMode midnightMode;

        private final static String keyPrefix = "yukon.common.dateFormatting.";

        public String getFormatKey() {
            return keyPrefix + name();
        }
        
        public MidnightMode getMidnightMode() {
            return midnightMode;
        }

        DateFormatEnum(MidnightMode midnightMode) {
            this.midnightMode = midnightMode;
        }
    }

    static public enum PeriodFormatEnum {
        HM_SHORT,
        S
    }

    public static enum MidnightMode {
        NORMAL,
        INCLUDES_MIDNIGHT
    };
    
    public static enum DateOnlyMode {
        START_OF_DAY, END_OF_DAY
    };

    /**
     * Convert an object to a string using the userContext for locale and timezone information.
     * The type dictates a suggested style, but not a specific format.
     * 
     * @param object - Object to format. Must be one of: Date, ReadableInstant, ReadablePartial
     * @param type
     * @param userContext
     * @return
     */
    public String format(Object object, DateFormatEnum type, YukonUserContext userContext);

    /**
     * This method is provided for completeness, but shouldn't not be used for regular code.
     * @param type
     * @param userContext
     * @return
     */
    public DateFormat getDateFormatter(DateFormatEnum type, YukonUserContext userContext);

    /**
     * Method to get a Joda DateTimeFormatter for the given format type 
     * @param type - Format to get
     * @param userContext - Current user context
     * @return Joda formatter
     */
    public DateTimeFormatter getDateTimeFormatter(DateFormatEnum type, YukonUserContext userContext);
    
    public Instant flexibleInstantParser(String dateStr, DateOnlyMode mode,
                                         YukonUserContext userContext) throws ParseException;

    public Date flexibleDateParser(String dateStr, DateOnlyMode mode,
                                   YukonUserContext userContext) throws ParseException;

    public Date flexibleDateParser(String dateStr, YukonUserContext userContext)
            throws ParseException;

    /**
     * This method returns a date formatted using the userContext's information with the exception 
     *  of the TimeZone.  The TimeZone is taken from the SystemUserContext.
     * TODO This method should be reevaluated soon (more specifically, how we parse dates that 
     *  are to be used by the server needs to be looked at.  Are the dates to be parsed per the CSR, 
     *  per the server, or per the device/customer? 
     * @param dateStr
     * @param mode
     * @param userContext
     * @return
     * @throws ParseException
     */
    public Date flexibleDateParserWithSystemTimeZone(String dateStr, DateOnlyMode mode,
            YukonUserContext userContext) throws ParseException;

    public LocalTime parseLocalTime(String localTimeStr,
            YukonUserContext userContext) throws ParseException;

    public LocalDate parseLocalDate(String localDateStr,
                                     YukonUserContext userContext) throws ParseException;
    
    public Calendar getCalendar(YukonUserContext userContext);

    /**
     * Format a period or duration using the given the specific type.
     * @param period must be an instance of ReadableDuration or ReadablePeriod
     */
    public String formatPeriod(Object period, PeriodFormatEnum type,
            YukonUserContext userContext) throws IllegalArgumentException;

    public PeriodFormatter getPeriodFormatter(PeriodFormatEnum type,
            YukonUserContext userContext);
}
