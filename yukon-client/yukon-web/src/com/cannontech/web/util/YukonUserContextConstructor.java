package com.cannontech.web.util;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class YukonUserContextConstructor implements YukonUserContextResolver {
    private AuthDao authDao;

    @Override
    public YukonUserContext resolveContext(HttpServletRequest request) {
        SimpleYukonUserContext context = new SimpleYukonUserContext();
        Locale locale = RequestContextUtils.getLocale(request);
        context.setLocale(locale);
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        context.setYukonUser(yukonUser);
        TimeZone timeZone = authDao.getUserTimeZone(yukonUser);
        context.setTimeZone(timeZone);
        
        return context;
    }
    
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
