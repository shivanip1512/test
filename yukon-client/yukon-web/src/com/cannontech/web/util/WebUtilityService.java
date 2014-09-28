package com.cannontech.web.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.JsonUtils;
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
    
    @SuppressWarnings("unchecked")
    public String getYukonCookieValue(HttpServletRequest request, String scope, String id, String defaultValue) 
            throws IOException {
        
        String result = defaultValue;
        
        Map<String, Cookie> cookies = new HashMap<String, Cookie>();
        for (Cookie cookie : request.getCookies()) cookies.put(cookie.getName(), cookie);
        
        Cookie yukon = cookies.get("yukon");
        
        if (yukon != null) {
            String json = yukon.getValue();
            String cookieText = URLDecoder.decode(json, "UTF-8");
            Map<String, String> data = JsonUtils.fromJson(cookieText, Map.class);
            
            String value = data.get(scope + id);
            if (value != null) result = value;
        }
        
        return result;
    }
    
}