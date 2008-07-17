package com.cannontech.web.login.access;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface UrlAccessChecker {

    public boolean hasUrlAccess(String urlPath, LiteYukonUser user);
    
    public boolean hasUrlAccess(HttpServletRequest request);
    
}
