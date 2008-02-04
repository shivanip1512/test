package com.cannontech.web.widget.support.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.util.MaxEntryLinkedHashMap;
import com.cannontech.web.widget.support.CachingWidgetParameterHelper;
import com.cannontech.web.widget.support.WidgetParameterHelper;


public class CachingWidgetParameterHelperImpl implements CachingWidgetParameterHelper, InitializingBean {
    
    private Integer maxCacheSize;
    private Boolean accessOrder;
    
    private MaxEntryLinkedHashMap<String, Object> cache;
    private String paramPrefix;
    
    public CachingWidgetParameterHelperImpl() {
        this.paramPrefix = getClass().getName();
    }
    
    public void afterPropertiesSet() {
        this.cache = new MaxEntryLinkedHashMap<String, Object>(maxCacheSize, accessOrder);
    }
    
    public String createParamName(String name, Object...uniqueIdentifiers) {
        return paramPrefix + "." + name + "." + StringUtils.join(uniqueIdentifiers, ".");
    }
    
    public synchronized Object getCachedParameter(HttpServletRequest request, String name, Object defaultVal, Object...uniqueIdentifiers) {
        
        Object foundVal = defaultVal;
        Boolean doCache = false;
        
        String cacheParamName = createParamName(name, uniqueIdentifiers);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredStringParameter(request, name);
            doCache = true;
        }
        catch (ServletRequestBindingException ex) {
            
            Object sessionVal = cache.get(cacheParamName);
            if (sessionVal != null) {
                foundVal = sessionVal;
                doCache = true;
            }
        }
        
        if (foundVal == defaultVal) {
            doCache = false;
        }
        
        if (doCache) {
            cache.put(cacheParamName, foundVal);
        }
        
        return foundVal;
    }
    
    public String getCachedStringParameter(HttpServletRequest request, String name, String defaultVal, Object...uniqueIdentifiers) {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal, uniqueIdentifiers);
        return foundVal;
    }
    
    public Boolean getCachedBooleanParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal, uniqueIdentifiers);
        return BOOLEAN_PARSER.parseBoolean(name, foundVal);
    }
    
    public Float getCachedFloatParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal, uniqueIdentifiers);
        return FLOAT_PARSER.parseFloat(name, foundVal);
    }
    
    public Double getCachedDoubleParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal, uniqueIdentifiers);
        return DOUBLE_PARSER.parseDouble(name, foundVal);
    }
    
    public Long getCachedLongParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal, uniqueIdentifiers);
        return LONG_PARSER.parseLong(name, foundVal);
    }
    
    public void removeFromCache(String name, Object...uniqueIdentifiers) {
        
        String cacheParamName = createParamName(name, uniqueIdentifiers);
        cache.remove(cacheParamName);
    }

    @Required
    public void setMaxCacheSize(Integer maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    @Required
    public void setAccessOrder(Boolean accessOrder) {
        this.accessOrder = accessOrder;
    }


}
