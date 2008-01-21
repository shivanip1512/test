package com.cannontech.web.i18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ThemeResolver;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.util.ServletUtil;

public class YukonGroupThemeResolver implements ThemeResolver {
    private AuthDao authDao;
    private String defaultThemeName = "";

    @Override
    public String resolveThemeName(HttpServletRequest request) {
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        String rolePropertyValue = authDao.getRolePropertyValue(yukonUser, WebClientRole.THEME_NAME);
        if (StringUtils.isBlank(rolePropertyValue)) {
            return defaultThemeName;
        }
        rolePropertyValue = rolePropertyValue.trim();
        return rolePropertyValue;
    }

    @Override
    public void setThemeName(HttpServletRequest request,
                             HttpServletResponse response,
                             String themeName) {
        throw new UnsupportedOperationException("Themes can only be changed via the standard role property mechanism");
    }
    
    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}
