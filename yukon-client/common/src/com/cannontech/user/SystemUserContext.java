package com.cannontech.user;

import java.util.Locale;
import java.util.TimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;

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
    public LiteYukonUser getYukonUser() {
        return UserUtils.getYukonUser();
    }

}
