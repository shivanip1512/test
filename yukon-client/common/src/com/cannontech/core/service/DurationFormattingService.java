package com.cannontech.core.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;

import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.user.YukonUserContext;

public interface DurationFormattingService {

    
    /**
     * Format a duration into a String. 
     * @param durationValue A value representing a duration in some time unit.
     * @param durationUnit The time unit that the duration value is given in.
     * @param type Type of format to use.
     * @param yukonUserContext Used for getting localized format text and a locale to the formatter.
     * @return
     */
    public String formatDuration(long durationValue, TimeUnit durationUnit, DurationFormat type, YukonUserContext yukonUserContext);

    /**
     * Method to get a formatted string representation of a time period.
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(Date startDate, Date endDate, DurationFormat type, YukonUserContext yukonUserContext);

    /**
     * Method to get a formatted string representation of a time period.
     * @param startDate - Start date of period
     * @param endDate - End date of period
     * @param type - Format type for the time period
     * @param yukonUserContext - Current user context
     * @return String representing time period in given format
     */
    public String formatDuration(ReadableInstant startDate, ReadableInstant endDate, DurationFormat type, YukonUserContext yukonUserContext);

    /**
     * Formats a duration by delegating to:
     * formatDuration(long durationValue, TimeUnit durationUnit, DurationFormat type, YukonUserContext yukonUserContext)
     *  
     * @param duration
     * @param type
     * @param yukonUserContext
     * @return String representing time period in given format
     */
    public String formatDuration(ReadableDuration duration, DurationFormat type, YukonUserContext yukonUserContext);

    /**
     * Formats a period into a string.
     * @param period
     * @param type
     * @param yukonUserContext
     * @return String representing time period in given format
     */
    public String formatPeriod(ReadablePeriod period, DurationFormat type, YukonUserContext yukonUserContext);

}