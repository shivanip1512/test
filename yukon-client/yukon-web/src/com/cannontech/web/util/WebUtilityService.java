package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

/**
 * A utility service to house methods solving common needs of controllers and views.
 */
public class WebUtilityService {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    
    /**
     * Returns the home url for the user, with the application context adjusted as well. 
     */
    public String getHomeUrl(HttpServletRequest request, LiteYukonUser user) {
        String homeUrl = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user);
        return ServletUtil.createSafeUrl(request, homeUrl);
    }
    
}