package com.cannontech.web.util;

import javax.servlet.ServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

public class ServletRequestEnumUtils {
    public static <T extends Enum<T>> T getEnumParameter(ServletRequest request, Class<T> type, String name) {
        return getEnumParameter(request, type, name, null);
    }
    
    public static <T extends Enum<T>> T getEnumParameter(ServletRequest request, Class<T> type, String name, T defaultValue) {
        String stringParameter = ServletRequestUtils .getStringParameter(request, name, null);
        if (stringParameter == null) {
            return defaultValue;
        } else {
            T value = Enum.valueOf(type, stringParameter);
            return value;
        }
    }
    
    public static <T extends Enum<T>> T getRequiredEnumParameter(ServletRequest request, Class<T> type, String name) throws ServletRequestBindingException {
        String stringParameter = ServletRequestUtils.getRequiredStringParameter(request, name);
        T value = Enum.valueOf(type, stringParameter);
        return value;
    }
}

