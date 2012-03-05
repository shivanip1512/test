package com.cannontech.web.util;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ThemeResolver;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class YukonUserContextConstructor implements YukonUserContextResolver {
    private AuthDao authDao;
    private ThemeResolver defaultThemeResolver;

    public void setDefaultThemeResolver(ThemeResolver defaultThemeResolver) {
        this.defaultThemeResolver = defaultThemeResolver;
    }

    @Override
    public YukonUserContext resolveContext(HttpServletRequest request) {
        SimpleYukonUserContext context = new SimpleYukonUserContext();
        Locale locale = RequestContextUtils.getLocale(request);
        context.setLocale(locale);
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        context.setYukonUser(yukonUser);
        TimeZone timeZone = authDao.getUserTimeZone(yukonUser);
        context.setTimeZone(timeZone);
        ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
        if (themeResolver == null) {
            themeResolver = defaultThemeResolver;
        }
        String themeName = themeResolver.resolveThemeName(request);
        context.setThemeName(themeName);
        return context;
    }
    
    @Override
    public YukonUserContext resolveContext(LiteYukonUser user, HttpServletRequest request) {
        SimpleYukonUserContext context = new SimpleYukonUserContext();
        Locale locale = RequestContextUtils.getLocale(request);
        context.setLocale(locale);
        context.setYukonUser(user);
        TimeZone timeZone = authDao.getUserTimeZone(user);
        context.setTimeZone(timeZone);
        ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
        if (themeResolver == null) {
            themeResolver = defaultThemeResolver;
        }
        String themeName = themeResolver.resolveThemeName(request);
        context.setThemeName(themeName);

        return context;
    }
    
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
