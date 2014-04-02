package com.cannontech.web.i18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ThemeResolver;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class YukonGroupThemeResolver implements ThemeResolver {
    private RolePropertyDao rolePropertyDao;
    private String defaultThemeName = "";

    @Override
    public String resolveThemeName(HttpServletRequest request) {
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        String rolePropertyValue = rolePropertyDao.getPropertyStringValue(
                                                       YukonRoleProperty.THEME_NAME,
                                                       yukonUser);
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

}
