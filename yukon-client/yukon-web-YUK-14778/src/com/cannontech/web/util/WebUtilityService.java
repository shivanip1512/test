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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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
    
    public <T> T getYukonCookieValue(HttpServletRequest req, String scope, String id, T defaultValue, 
            TypeReference<T> expected) throws IOException {
        
        T result = defaultValue;
        
        Map<String, Cookie> cookies = new HashMap<String, Cookie>();
        for (Cookie cookie : req.getCookies()) cookies.put(cookie.getName(), cookie);
        
        Cookie yukon = cookies.get("yukon");
        
        if (yukon != null) {
            String json = yukon.getValue();
            String cookieText = URLDecoder.decode(json, "UTF-8");
            Map<String, Object> data = JsonUtils.fromJson(cookieText, new TypeReference<Map<String, Object>>() {});
            
            Object mapValue = data.get(scope + id);
            String stringify = JsonUtils.toJson(mapValue);
            try {
                T value = JsonUtils.fromJson(stringify, expected);
                if (value != null) result = value;
            } catch (InvalidFormatException e) {
                //Just use the default
            }
        }
        
        return result;
    }
    
}