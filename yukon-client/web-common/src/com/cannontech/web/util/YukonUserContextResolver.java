package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface YukonUserContextResolver {
    public YukonUserContext resolveContext(HttpServletRequest request);
    
    public YukonUserContext resolveContext(LiteYukonUser user, HttpServletRequest request);

}
