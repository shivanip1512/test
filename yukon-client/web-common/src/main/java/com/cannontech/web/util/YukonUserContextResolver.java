package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface YukonUserContextResolver {
    public YukonUserContext resolveContext(HttpServletRequest request);
    
    /**
     * This method should not be used in most cases.  It should only be used in the few cases when we need a user context
     * but do not have a user in the request.
     */
    public YukonUserContext resolveContext(LiteYukonUser user, HttpServletRequest request);

}
