package com.cannontech.core.service;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.cannontech.common.exception.BadConfigurationException;


public interface SystemDateFormattingService {
    
    public static enum DateFormatEnum {
        PeakReport_DateOnly("MM/dd/yyyy"),
        PeakReport_DateTime("MM/dd/yy hh:mm:ss"),
        LoadProfile("MM/dd/yyyy HH:mm");
        
        private final String formatString;

        private DateFormatEnum(String formatString) {
            this.formatString = formatString;
        }
        
        public String getFormatString() {
            return formatString;
        }
    }

    /**
     * This method returns the TimeZone for the System.  
     * Calls getUserTimeZone(null)...the ConfigurationRole.DEFAULT_TIMEZONE role property is checked, if found return
     * else if isBlank, then return the server timezone.
     * Throws BadConfigurationException when timeZone string value is not valid. 
     * @param user
     * @return
     */
    public TimeZone getSystemTimeZone() throws BadConfigurationException;
    
    /**
     * This method returns a SimpleDateFormat with format syntax of dateFormat, 
     *  for the system configuration (timezone).
     * @param dateFormat
     * @return SimpleDateFormat
     */
    public DateFormat getSystemDateFormat(DateFormatEnum dateFormat);
    
    /**
     * This method returns a new Calendar object for the system TimeZone.
     * @return
     */
    public Calendar getSystemCalendar();
}