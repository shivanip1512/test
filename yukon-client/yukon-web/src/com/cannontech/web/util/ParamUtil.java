package com.cannontech.web.util;

import javax.servlet.ServletRequest;

import com.cannontech.util.GetterUtil;

/**
 * Util for getting parameters from the request object allowing for
 * a default value if the key is not found.
 */
public class ParamUtil {

    public static int getInteger(ServletRequest req, String param, int defaultValue) {
        return GetterUtil.get(req.getParameter(param), defaultValue);
    }

    public static String getString(ServletRequest req, String param, String defaultValue) {
        String returnValue = GetterUtil.get(req.getParameter(param), defaultValue);

        if (returnValue != null) {
            return returnValue.trim();
        }

        return null;
    }
}