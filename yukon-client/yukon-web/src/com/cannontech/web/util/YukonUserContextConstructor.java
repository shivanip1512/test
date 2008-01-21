package com.cannontech.web.util;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class YukonUserContextConstructor implements YukonUserContextResolver {
    private YukonUserDao yukonUserDao;

    @Override
    public YukonUserContext resolveContext(HttpServletRequest request) {
        SimpleYukonUserContext context = new SimpleYukonUserContext();
        Locale locale = RequestContextUtils.getLocale(request);
        context.setLocale(locale);
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        context.setYukonUser(yukonUser);
        TimeZone timeZone = yukonUserDao.getUserTimeZone(yukonUser);
        context.setTimeZone(timeZone);
        
        return context;
    }
    
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

}
