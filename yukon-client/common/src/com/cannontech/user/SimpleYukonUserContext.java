package com.cannontech.user;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.database.data.lite.LiteYukonUser;

public class SimpleYukonUserContext implements YukonUserContext {
    private Locale locale;
    private LiteYukonUser yukonUser;
    private TimeZone timeZone;
    private String themeName;
    public String getThemeName() {
        return themeName;
    }
    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
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
    public SimpleYukonUserContext(LiteYukonUser yukonUser, Locale locale, TimeZone timeZone, String themeName) {
        this.yukonUser = yukonUser;
        this.locale = locale;
        this.timeZone = timeZone;
        this.themeName = themeName;
    }
    public SimpleYukonUserContext() {
    }
    
    @Override
    public String toString() {
        ToStringCreator to = new ToStringCreator(this);
        to.append("yukonUser", yukonUser);
        to.append("timeZone", timeZone);
        to.append("locale", locale);
        to.append("themeName", themeName);
        return to.toString();
    }
}
