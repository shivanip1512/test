package com.cannontech.web.widget.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.web.widget.support.WidgetParameterHelper.BooleanParser;
import com.cannontech.web.widget.support.WidgetParameterHelper.DoubleParser;
import com.cannontech.web.widget.support.WidgetParameterHelper.FloatParser;
import com.cannontech.web.widget.support.WidgetParameterHelper.IntParser;
import com.cannontech.web.widget.support.WidgetParameterHelper.LongParser;


public interface CachingWidgetParameterHelper {
    
    public static final IntParser INT_PARSER = new IntParser();
    public static final LongParser LONG_PARSER = new LongParser();
    public static final FloatParser FLOAT_PARSER = new FloatParser();
    public static final DoubleParser DOUBLE_PARSER = new DoubleParser();
    public static final BooleanParser BOOLEAN_PARSER = new BooleanParser();
    
    public abstract Object getCachedParameter(HttpServletRequest request, String name, Object defaultVal, Object...uniqueIdentifiers);
    
    public abstract String getCachedStringParameter(HttpServletRequest request, String name, String defaultVal, Object...uniqueIdentifiers);
    public abstract Boolean getCachedBooleanParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException;
    public abstract Float getCachedFloatParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException;
    public abstract Double getCachedDoubleParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException;
    public abstract Long getCachedLongParameter(HttpServletRequest request, String name, Boolean defaultVal, Object...uniqueIdentifiers) throws ServletRequestBindingException;
    
    public abstract void removeFromCache(String name, Object...uniqueIdentifiers);

    public abstract String createParamName(String name, Object...uniqueIdentifiers);
}
