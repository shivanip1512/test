package com.cannontech.web.widget.support.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.util.MaxEntryLinkedHashMap;
import com.cannontech.web.widget.support.WidgetParameterGrabber;
import com.cannontech.web.widget.support.WidgetParameterHelper;


public class CachingWidgetParameterGrabber implements WidgetParameterGrabber {
    
    private Integer maxCacheSize;
    private Boolean accessOrder;
    private Boolean useCache;
    private List<String> uniqueIdentifiers = Collections.emptyList();
    
    private MaxEntryLinkedHashMap<String, Object> cache;
    private String paramPrefix;
    
    public CachingWidgetParameterGrabber() {
        this.paramPrefix = getClass().getName();
    }
    
    @PostConstruct
    public void init() {
        this.cache = new MaxEntryLinkedHashMap<String, Object>(maxCacheSize, accessOrder);
    }
    
    public String createParamName(String name) {
        return paramPrefix + "." + name + "." + StringUtils.join(uniqueIdentifiers, ".");
    }
    
    public synchronized Object getCachedParameter(HttpServletRequest request, String name, Object defaultVal) {
        
        Object foundVal = defaultVal;
        Boolean doCache = false;
        
        String cacheParamName = createParamName(name);
        
        try {
            foundVal = WidgetParameterHelper.getRequiredStringParameter(request, name);
            doCache = true;
        }
        catch (ServletRequestBindingException ex) {
            
            if (useCache) {
                Object sessionVal = cache.get(cacheParamName);
                if (sessionVal != null) {
                    foundVal = sessionVal;
                    doCache = true;
                }
            }
        }
        
        if (foundVal == defaultVal) {
            doCache = false;
        }
        
        if (useCache && doCache) {
            cache.put(cacheParamName, foundVal);
        }
        
        return foundVal;
    }
    
    public String getCachedStringParameter(HttpServletRequest request, String name, String defaultVal) {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal);
        return foundVal;
    }
    
    public Boolean getCachedBooleanParameter(HttpServletRequest request, String name, Boolean defaultVal) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal);
        return BOOLEAN_PARSER.parseBoolean(name, foundVal);
    }
    
    public Float getCachedFloatParameter(HttpServletRequest request, String name, Boolean defaultVal) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal);
        return FLOAT_PARSER.parseFloat(name, foundVal);
    }
    
    public Double getCachedDoubleParameter(HttpServletRequest request, String name, Boolean defaultVal) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal);
        return DOUBLE_PARSER.parseDouble(name, foundVal);
    }
    
    public Long getCachedLongParameter(HttpServletRequest request, String name, Boolean defaultVal) throws ServletRequestBindingException {
        
        String foundVal = (String)getCachedParameter(request, name, defaultVal);
        return LONG_PARSER.parseLong(name, foundVal);
    }
    
    public void removeFromCache(String name) {
        
        String cacheParamName = createParamName(name);
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

    @Required
    public void setUseCache(Boolean useCache) {
        this.useCache = useCache;
    }

    // not required but should be set when using caching
    public void setUniqueIdentifiers(List<String> uniqueIdentifiers) {
        this.uniqueIdentifiers = uniqueIdentifiers;
    }



}
