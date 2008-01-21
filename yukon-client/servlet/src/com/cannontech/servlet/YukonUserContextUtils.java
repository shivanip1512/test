package com.cannontech.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

import com.cannontech.user.YukonUserContext;

public class YukonUserContextUtils {
    // this used to be in the filter, but it caused classpath problem with servlet and yukon-web
    public static final String userContextAttrName = YukonUserContextUtils.class.getName() + ".USER_CONTEXT";
    public static YukonUserContext getYukonUserContext(HttpServletRequest request) {
        Object attribute = request.getAttribute(userContextAttrName);
        return processAttribute(attribute);
    }

    public static YukonUserContext getYukonUserContext(JspContext jspContext) {
        Object attribute = jspContext.getAttribute(userContextAttrName, PageContext.REQUEST_SCOPE);
        return processAttribute(attribute);
    }
    
    private static YukonUserContext processAttribute(Object attribute) {
        if (attribute == null) {
            throw new RuntimeException("Expected context to be attached, but it could not be found");
        } else if (attribute instanceof Throwable) {
            Throwable throwable = (Throwable) attribute;
            throw new RuntimeException("Expected context to be attached, but found an error", throwable);
        } else if (attribute instanceof YukonUserContext) {
            YukonUserContext context = (YukonUserContext) attribute;
            return context;
        } else {
            throw new RuntimeException("Expected context to be attached, but found something else: " + attribute);
        }
    }
}
