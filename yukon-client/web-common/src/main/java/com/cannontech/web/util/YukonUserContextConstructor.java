package com.cannontech.web.util;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ThemeResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class YukonUserContextConstructor implements YukonUserContextResolver {
    private AuthDao authDao;
    private ThemeResolver defaultThemeResolver;
    @Autowired private RolePropertyDao rolePropertyDao;

    public void setDefaultThemeResolver(ThemeResolver defaultThemeResolver) {
        this.defaultThemeResolver = defaultThemeResolver;
    }

    @Override
    public YukonUserContext resolveContext(HttpServletRequest request) {
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        return resolveContext(user, request);
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
        try {
            String themeName = themeResolver.resolveThemeName(request);
            context.setThemeName(themeName);
        } catch (Exception e) {
            String rolePropertyValue = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.THEME_NAME, user);
           if (StringUtils.isBlank(rolePropertyValue)) {
               rolePropertyValue = "";
           }
           rolePropertyValue = rolePropertyValue.trim();
           context.setThemeName(rolePropertyValue);
        }
        return context;
    }
    
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
