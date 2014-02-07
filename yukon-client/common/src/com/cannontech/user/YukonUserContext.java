package com.cannontech.user;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.ThemeUtils;

/**
 * This interface represents a simple data object used to pass around information
 * about the current user for internationalization and security purposes.
 * 
 * Instances of the class must be "serializable". This is the primary reason
 * that this class doesn't have convenient helpers like "getMessageSource".
 */
public interface YukonUserContext extends Serializable {
    YukonUserContext system = new YukonUserContext() {
        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }

        @Override
        public TimeZone getTimeZone() {
            return TimeZone.getDefault();
        }

        @Override
        public DateTimeZone getJodaTimeZone() {
            return DateTimeZone.getDefault();
        }

        @Override
        public LiteYukonUser getYukonUser() {
            return UserUtils.getYukonUser();
        }

        @Override
        public String getThemeName() {
            return ThemeUtils.getDefaultThemeName();
        }
    };

    Locale getLocale();

    LiteYukonUser getYukonUser();

    TimeZone getTimeZone();

    DateTimeZone getJodaTimeZone();

    String getThemeName();
}
