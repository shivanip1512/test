package com.cannontech.web.util;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public final class SpringWebUtil {

    public static final LiteYukonUser getYukonUser() throws ServletRequestBindingException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        
        LiteYukonUser user = (LiteYukonUser) requestAttributes.getAttribute(ServletUtil.ATT_YUKON_USER, 
                                                            RequestAttributes.SCOPE_SESSION);
        
        if (user == null) {
            throw new ServletRequestBindingException("Required user not found in request.");
        }
        
        return user;
        
    }
    
}
