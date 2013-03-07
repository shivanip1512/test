package com.cannontech.user;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This interface represents a simple data object used to pass around information
 * about the current user for internationalization and security purposes.
 * 
 * Instances of the class must be "serializable". This is the primary reason
 * that this class doesn't have convenient helpers like "getMessageSource".
 */
public interface YukonUserContext extends Serializable {

    public Locale getLocale();

    public LiteYukonUser getYukonUser();

    public TimeZone getTimeZone();

    public DateTimeZone getJodaTimeZone();
    
    public String getThemeName();

}