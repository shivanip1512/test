package com.cannontech.core.service;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.cannontech.common.exception.BadConfigurationException;


public interface SystemDateFormattingService{

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
    public SimpleDateFormat getSystemDateFormat(String dateFormatStr);
}