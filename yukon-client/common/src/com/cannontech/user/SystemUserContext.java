package com.cannontech.user;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.ThemeUtils;

/**
 * @deprecated use {@link YukonUserContext#system}
 */
@Deprecated
public class SystemUserContext implements YukonUserContext {

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
}
