package com.cannontech.user;

import java.util.Locale;
import java.util.TimeZone;

import com.cannontech.database.data.lite.LiteYukonUser;

public class SimpleYukonUserContext implements YukonUserContext {
    private Locale locale;
    private LiteYukonUser yukonUser;
    private TimeZone timeZone;
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }
    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }
    public TimeZone getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    public SimpleYukonUserContext(LiteYukonUser yukonUser, Locale locale, TimeZone timeZone) {
        this.yukonUser = yukonUser;
        this.locale = locale;
        this.timeZone = timeZone;
    }
    public SimpleYukonUserContext() {
    }
}
