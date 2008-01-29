package com.cannontech.web.widget.support;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;

public class CachingWidgetParameterHelper {
    
    private final String widgetName;
    private final HttpServletRequest request;
    private final String attrPrefix;

    public CachingWidgetParameterHelper(HttpServletRequest request, String widgetName) {
        this.request = request;
        this.widgetName = widgetName;
        this.attrPrefix = getClass().getName() + "." + this.widgetName;
    }
    
    /**
     * Get a String parameter.
     * If not found in request, the session will be check for any attribute matching the sessionAttrName.
     * If not found in session, the defaultVal will be returned. Never throws an exception.
     * @param request
     * @param name
     * @param defaultVal
     * @param sessionAttrName
     * @return
     */
    public String getStringParameter(String name, String defaultVal, Object...uniqueIdentifiers) {
        
        String foundVal = defaultVal;
        
        String sessionAttrName = createAttrName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredStringParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            
            String sessionVal = (String)getSessionAttribute(request, sessionAttrName);
            if (sessionVal != null) {
                foundVal = sessionVal;
            }
        }
        
        addSessionValue(request, sessionAttrName, foundVal);
        return foundVal;
    }
    
    /**
     * Get a Integer parameter.
     * If not found in request, the session will be check for any attribute matching the sessionAttrName.
     * If not found in session, the defaultVal will be returned. Never throws an exception.
     * @param request
     * @param name
     * @param defaultVal
     * @param sessionAttrName
     * @return
     */
    public Integer getIntParameter(String name, Integer defaultVal, Object...uniqueIdentifiers) {
        
        Integer foundVal = defaultVal;
        
        String sessionAttrName = createAttrName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredIntParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            
            Integer sessionVal = (Integer)getSessionAttribute(request, sessionAttrName);
            if (sessionVal != null) {
                foundVal = sessionVal;
            }
        }
        
        addSessionValue(request, sessionAttrName, foundVal);
        return foundVal;
    }
    
    /**
     * Get a Long parameter.
     * If not found in request, the session will be check for any attribute matching the sessionAttrName.
     * If not found in session, the defaultVal will be returned. Never throws an exception.
     * @param request
     * @param name
     * @param defaultVal
     * @param sessionAttrName
     * @return
     */
    public Long getLongParameter(String name, Long defaultVal, Object...uniqueIdentifiers) {
        
        Long foundVal = defaultVal;
        
        String sessionAttrName = createAttrName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredLongParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            
            Long sessionVal = (Long)getSessionAttribute(request, sessionAttrName);
            if (sessionVal != null) {
                foundVal = sessionVal;
            }
        }
        
        addSessionValue(request, sessionAttrName, foundVal);
        return foundVal;
    }
    
    /**
     * Get a Boolean parameter.
     * If not found in request, the session will be check for any attribute matching the sessionAttrName.
     * If not found in session, the defaultVal will be returned. Never throws an exception.
     * @param request
     * @param name
     * @param defaultVal
     * @param sessionAttrName
     * @return
     */
    public Boolean getBooleanParameter(String name, Boolean defaultVal, Object...uniqueIdentifiers) {
        
        Boolean foundVal = defaultVal;
        
        String sessionAttrName = createAttrName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredBooleanParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            
            Boolean sessionVal = (Boolean)getSessionAttribute(request, sessionAttrName);
            if (sessionVal != null) {
                foundVal = sessionVal;
            }
        }
        
        addSessionValue(request, sessionAttrName, foundVal);
        return foundVal;
    }
    
    /**
     * Get a Float parameter.
     * If not found in request, the session will be check for any attribute matching the sessionAttrName.
     * If not found in session, the defaultVal will be returned. Never throws an exception.
     * @param request
     * @param name
     * @param defaultVal
     * @param sessionAttrName
     * @return
     */
    public Float getFloatParameter(String name, Float defaultVal, Object...uniqueIdentifiers) {
        
        Float foundVal = defaultVal;
        
        String sessionAttrName = createAttrName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredFloatParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            
            Float sessionVal = (Float)getSessionAttribute(request, sessionAttrName);
            if (sessionVal != null) {
                foundVal = sessionVal;
            }
        }
        
        addSessionValue(request, sessionAttrName, foundVal);
        return foundVal;
    }
    
    /**
     * Get a Double parameter.
     * If not found in request, the session will be check for any attribute matching the sessionAttrName.
     * If not found in session, the defaultVal will be returned. Never throws an exception.
     * @param request
     * @param name
     * @param defaultVal
     * @param sessionAttrName
     * @return
     */
    public Double getDoubleParameter(String name, Double defaultVal, Object...uniqueIdentifiers) {
        
        Double foundVal = defaultVal;
        
        String sessionAttrName = createAttrName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredDoubleParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            
            Double sessionVal = (Double)getSessionAttribute(request, sessionAttrName);
            if (sessionVal != null) {
                foundVal = sessionVal;
            }
        }
        
        addSessionValue(request, sessionAttrName, foundVal);
        return foundVal;
    }
    
    
    private static void addSessionValue(HttpServletRequest request, String sessionAttrName, Object value) {
        HttpSession session = request.getSession();
        session.setAttribute(sessionAttrName, value);
    }
    
    private static Object getSessionAttribute(ServletRequest request, String sessionAttrName) {
        HttpSession session = ((HttpServletRequest)request).getSession();
        return session.getAttribute(sessionAttrName);
    }
    
    private String createAttrName(String name, Object...uniqueIdentifiers) {
        return attrPrefix + "." + name + "." + StringUtils.join(uniqueIdentifiers, ".");
    }

}
