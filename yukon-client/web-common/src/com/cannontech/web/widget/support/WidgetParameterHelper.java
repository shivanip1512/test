/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cannontech.web.widget.support;

import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.Validate;
import org.springframework.web.bind.ServletRequestBindingException;

/**
 * Widget parameter extraction methods.
 * 
 * <p>I'm using this Spring code as a starting point for my widget parameter
 * extraction class.
 * 
 * <p>Parameter extraction methods, for an approach distinct from data binding,
 * in which parameters of specific types are required.
 *
 * <p>This approach is very useful for simple submissions, where binding
 * request parameters to a command object would be overkill.
 * 
 * 
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @since 2.0
 */
public abstract class WidgetParameterHelper {

    private static final IntParser INT_PARSER = new IntParser();

    private static final LongParser LONG_PARSER = new LongParser();

    private static final FloatParser FLOAT_PARSER = new FloatParser();

    private static final DoubleParser DOUBLE_PARSER = new DoubleParser();

    private static final BooleanParser BOOLEAN_PARSER = new BooleanParser();


    private static String getParameter(ServletRequest request, String name) {
        return getParameterMap(request).get(name);
    }
    
    private static Map<String, String> getParameterMap(ServletRequest request) {
        Object parameters = request.getAttribute("widgetParameters");
        if (parameters == null) {
            return Collections.emptyMap();
        }
        Validate.isTrue(parameters instanceof Map, "widget parameters aren't a map???");
        Map<String, String> result = (Map<String, String>) parameters;
        return result;
    }
    

    /**
     * Get an Integer parameter, or <code>null</code> if not present.
     * Throws an exception if it the parameter value isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @return the Integer value, or <code>null</code> if not present
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static Integer getIntParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        if (getParameter(request, name) == null) {
            return null;
        }
        return new Integer(getRequiredIntParameter(request, name));
    }

    /**
     * Get an int parameter, with a fallback value. Never throws an exception.
     * Can pass a distinguished value as default to enable checks of whether it was supplied.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @param defaultVal the default value to use as fallback
     */
    public static int getIntParameter(ServletRequest request, String name, int defaultVal) {
        try {
            return getRequiredIntParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }

    /**
     * Get an int parameter, throwing an exception if it isn't found or isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static int getRequiredIntParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        String parameter = getParameter(request, name);
        return INT_PARSER.parseInt(name, parameter);
    }

    /**
     * Get a Long parameter, or <code>null</code> if not present.
     * Throws an exception if it the parameter value isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @return the Long value, or <code>null</code> if not present
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static Long getLongParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        if (getParameter(request, name) == null) {
            return null;
        }
        return new Long(getRequiredLongParameter(request, name));
    }

    /**
     * Get a long parameter, with a fallback value. Never throws an exception.
     * Can pass a distinguished value as default to enable checks of whether it was supplied.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @param defaultVal the default value to use as fallback
     */
    public static long getLongParameter(ServletRequest request, String name, long defaultVal) {
        try {
            return getRequiredLongParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }

    /**
     * Get a long parameter, throwing an exception if it isn't found or isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static long getRequiredLongParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        String parameter = getParameter(request, name);
        return LONG_PARSER.parseLong(name, parameter);
    }

    /**
     * Get a Float parameter, or <code>null</code> if not present.
     * Throws an exception if it the parameter value isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @return the Float value, or <code>null</code> if not present
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static Float getFloatParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        if (getParameter(request, name) == null) {
            return null;
        }
        return new Float(getRequiredFloatParameter(request, name));
    }

    /**
     * Get a float parameter, with a fallback value. Never throws an exception.
     * Can pass a distinguished value as default to enable checks of whether it was supplied.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @param defaultVal the default value to use as fallback
     */
    public static float getFloatParameter(ServletRequest request, String name, float defaultVal) {
        try {
            return getRequiredFloatParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }

    /**
     * Get a float parameter, throwing an exception if it isn't found or isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static float getRequiredFloatParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        String parameter = getParameter(request, name);
        return FLOAT_PARSER.parseFloat(name, parameter);
    }

    /**
     * Get a Double parameter, or <code>null</code> if not present.
     * Throws an exception if it the parameter value isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @return the Double value, or <code>null</code> if not present
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static Double getDoubleParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        if (getParameter(request, name) == null) {
            return null;
        }
        return new Double(getRequiredDoubleParameter(request, name));
    }

    /**
     * Get a double parameter, with a fallback value. Never throws an exception.
     * Can pass a distinguished value as default to enable checks of whether it was supplied.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @param defaultVal the default value to use as fallback
     */
    public static double getDoubleParameter(ServletRequest request, String name, double defaultVal) {
        try {
            return getRequiredDoubleParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }

    /**
     * Get a double parameter, throwing an exception if it isn't found or isn't a number.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static double getRequiredDoubleParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        String parameter = getParameter(request, name);
        return DOUBLE_PARSER.parseDouble(name, parameter);
    }

    /**
     * Get a Boolean parameter, or <code>null</code> if not present.
     * Throws an exception if it the parameter value isn't a boolean.
     * <p>Accepts "true", "on", "yes" (any case) and "1" as values for true;
     * treats every other non-empty value as false (i.e. parses leniently).
     * @param request current HTTP request
     * @param name the name of the parameter
     * @return the Boolean value, or <code>null</code> if not present
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static Boolean getBooleanParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        if (getParameter(request, name) == null) {
            return null;
        }
        return (getRequiredBooleanParameter(request, name) ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Get a boolean parameter, with a fallback value. Never throws an exception.
     * Can pass a distinguished value as default to enable checks of whether it was supplied.
     * <p>Accepts "true", "on", "yes" (any case) and "1" as values for true;
     * treats every other non-empty value as false (i.e. parses leniently).
     * @param request current HTTP request
     * @param name the name of the parameter
     * @param defaultVal the default value to use as fallback
     */
    public static boolean getBooleanParameter(ServletRequest request, String name, boolean defaultVal) {
        try {
            return getRequiredBooleanParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }

    /**
     * Get a boolean parameter, throwing an exception if it isn't found
     * or isn't a boolean.
     * <p>Accepts "true", "on", "yes" (any case) and "1" as values for true;
     * treats every other non-empty value as false (i.e. parses leniently).
     * @param request current HTTP request
     * @param name the name of the parameter
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static boolean getRequiredBooleanParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        String parameter = getParameter(request, name);
        return BOOLEAN_PARSER.parseBoolean(name, parameter);
    }

    /**
     * Get a String parameter, or <code>null</code> if not present.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @return the String value, or <code>null</code> if not present
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static String getStringParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {

        if (getParameter(request, name) == null) {
            return null;
        }
        return getRequiredStringParameter(request, name);
    }

    /**
     * Get a String parameter, with a fallback value. Never throws an exception.
     * Can pass a distinguished value to default to enable checks of whether it was supplied.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @param defaultVal the default value to use as fallback
     */
    public static String getStringParameter(ServletRequest request, String name, String defaultVal) {
        try {
            return getRequiredStringParameter(request, name);
        }
        catch (ServletRequestBindingException ex) {
            return defaultVal;
        }
    }

    /**
     * Get a String parameter, throwing an exception if it isn't found.
     * @param request current HTTP request
     * @param name the name of the parameter
     * @throws ServletRequestBindingException a subclass of ServletException,
     * so it doesn't need to be caught
     */
    public static String getRequiredStringParameter(ServletRequest request, String name)
            throws ServletRequestBindingException {
        String parameter = getParameter(request, name);
        if (parameter == null) {
            throw new ServletRequestBindingException("Parameter " + name + " doesn't exist");
        }
        return parameter.toString();
    }



    private abstract static class ParameterParser {

        protected final Object parse(String name, String parameter) throws ServletRequestBindingException {
            validateRequiredParameter(name, parameter);
            try {
                return doParse(parameter);
            }
            catch (NumberFormatException ex) {
                throw new ServletRequestBindingException(
                        "Required " + getType() + " parameter '" + name + "' with value of '" +
                        parameter + "' is not a valid number");
            }
        }

        protected final void validateRequiredParameter(String name, Object parameter)
                throws ServletRequestBindingException {

            if (parameter == null) {
                throw new ServletRequestBindingException(
                        "Required " + getType() + " parameter '" + name + "' is not present");
            }
        }

        protected abstract String getType();

        protected abstract Object doParse(String parameter) throws NumberFormatException;
    }


    public static class IntParser extends ParameterParser {

        protected String getType() {
            return "int";
        }

        protected Object doParse(String s) throws NumberFormatException {
            return Integer.valueOf((String)s);
        }

        public int parseInt(String name, String parameter) throws ServletRequestBindingException {
            return ((Number) parse(name, parameter)).intValue();
        }

    }


    public static class LongParser extends ParameterParser {

        protected String getType() {
            return "long";
        }

        protected Object doParse(String parameter) throws NumberFormatException {
            return Long.valueOf(parameter);
        }

        public long parseLong(String name, String parameter) throws ServletRequestBindingException {
            return ((Number) parse(name, parameter)).longValue();
        }

    }


    public static class FloatParser extends ParameterParser {

        protected String getType() {
            return "float";
        }

        protected Object doParse(String parameter) throws NumberFormatException {
            return Float.valueOf(parameter);
        }

        public float parseFloat(String name, String parameter) throws ServletRequestBindingException {
            return ((Number) parse(name, parameter)).floatValue();
        }

    }


    public static class DoubleParser extends ParameterParser {

        protected String getType() {
            return "double";
        }

        protected Object doParse(String parameter) throws NumberFormatException {
            return Double.valueOf(parameter);
        }

        public double parseDouble(String name, String parameter) throws ServletRequestBindingException {
            return ((Number) parse(name, parameter)).doubleValue();
        }

    }


    public static class BooleanParser extends ParameterParser {

        protected String getType() {
            return "boolean";
        }

        protected Object doParse(String parameter) throws NumberFormatException {
            return (parameter.equalsIgnoreCase("true") || parameter.equalsIgnoreCase("on") ||
                    parameter.equalsIgnoreCase("yes") || parameter.equals("1") ? Boolean.TRUE : Boolean.FALSE);
        }

        public boolean parseBoolean(String name, String parameter) throws ServletRequestBindingException {
            return ((Boolean) parse(name, parameter)).booleanValue();
        }

    }


}
