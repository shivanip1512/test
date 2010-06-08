package com.cannontech.core.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.ReadableInstant;

import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;

public interface DurationFormattingService {

    /**
     * Format a duration into a String.
     * @param durationValue A value representing a duration in some time unit.
     * @param durationUnit The time unit that the duration value is given in.
     * @param type Type of format to use.
     * @param roundRightmostUp Specifies if the rightmost field will be rounded up.
         *                     - milliseconds >= 500 are rounded up to an additional second
         *                     - seconds >= 30 are rounded up to an additional minute
         *                     - minutes >= 30 are rounded up to an additional hours
     * @param yukonUserContext Used for getting localized format text and a locale to the formatter.
     * @return
     */
    public String formatDuration(long durationValue, TimeUnit durationUnit, DurationFormat type, boolean roundRightmostUp, YukonUserContext yukonUserContext);
    
    /**
     * Format a duration into a String. 
     * Always uses default rounding mode of the DurationFormat used.
     * @param durationValue A value representing a duration in some time unit.
     * @param durationUnit The time unit that the duration value is given in.
     * @param type Type of format to use.
     * @param yukonUserContext Used for getting localized format text and a locale to the formatter.
     * @return
     */
    public String formatDuration(long durationValue, TimeUnit durationUnit, DurationFormat type, YukonUserContext yukonUserContext);

    /**
     * Method to get a formatted string representation of a time period.
     * Always uses default rounding mode of the DurationFormat used.
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param roundRightmostUp Specifies if the rightmost field will be rounded up.
     *                         - milliseconds >= 500 are rounded up to an additional second
     *                         - seconds >= 30 are rounded up to an additional minute
     *                         - minutes >= 30 are rounded up to an additional hours
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(Date startDate, Date endDate, DurationFormat type, boolean roundRightmostUp, YukonUserContext yukonUserContext);

    /**
     * Method to get a formatted string representation of a time period.
     * Always uses default rounding mode of the DurationFormat used.
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param roundRightmostUp Specifies if the rightmost field will be rounded up.
     *                         - milliseconds >= 500 are rounded up to an additional second
     *                         - seconds >= 30 are rounded up to an additional minute
     *                         - minutes >= 30 are rounded up to an additional hours
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(ReadableInstant startDate, ReadableInstant endDate, 
                                  DurationFormat type, boolean roundRightmostUp, 
                                  YukonUserContext yukonUserContext);
    
    /**
     * Method to get a formatted string representation of a time period.
     * Always uses default rounding mode of the DurationFormat used.
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(Date startDate, Date endDate, DurationFormat type, 
                                  YukonUserContext yukonUserContext);

    /**
     * Method to get a formatted string representation of a time period.
     * Always uses default rounding mode of the DurationFormat used.
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(ReadableInstant startDate, ReadableInstant endDate, 
                                  DurationFormat type, YukonUserContext yukonUserContext);

}
