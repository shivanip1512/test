package com.cannontech.web.login.access;

import javax.servlet.http.HttpServletRequest;

public interface UrlAccessChecker {

    public boolean hasUrlAccess(HttpServletRequest request);
    
}
