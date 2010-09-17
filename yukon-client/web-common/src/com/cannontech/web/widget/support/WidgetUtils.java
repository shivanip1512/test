package com.cannontech.web.widget.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jsonOLD.JSONArray;
import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

import com.cannontech.web.PageEditMode;

public class WidgetUtils {
    
    public static Map<String,String> combineParameters(Map<String,String> outer, Map<String,? extends Object> inner) {
        Map<String,String> emptyMap1 = Collections.emptyMap();
        outer = (outer != null ? outer : emptyMap1);
        Map<String,Object> emptyMap2 = Collections.emptyMap();
        inner = (inner != null ? inner : emptyMap2);
        Map<String,String> result = Collections.checkedMap(new HashMap<String,String>(), String.class, String.class);
        result.putAll(outer);
        for (Map.Entry<String,? extends Object> entry : inner.entrySet()) {
            if (entry.getValue() == null) {
                result.put(entry.getKey(), null);
            } else {
                result.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return result;
    }
    
    public static String generateJsonString(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "\"" + StringEscapeUtils.escapeJavaScript((String) obj) + "\"";
        } else if (obj instanceof Map<?, ?>) {
            return new JSONObject((Map<?, ?>)obj).toString();
        } else if (obj instanceof Collection<?>) {
            return new JSONArray((Collection<?>)obj).toString();
        } else if (obj instanceof Boolean) {
        	Boolean boolObj = (Boolean)obj;
        	return boolObj.toString();
        } else if (obj instanceof PageEditMode) {
        	PageEditMode pageEditModeObj = (PageEditMode)obj;
        	return pageEditModeObj.name();
        } else if (obj.getClass().isArray()) {
            return JSONArray.fromObject(obj).toString();
        } else {
            return JSONObject.fromObject(obj).toString();
        }
    }
    
    public static String generateJsonString2(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "\"" + StringEscapeUtils.escapeJavaScript((String) obj) + "\"";
        } else if (obj instanceof Map<?, ?>) {
            //have not confirmed that this works
            return net.sf.json.JSONObject.fromObject(obj).toString();
        } else if (obj instanceof Collection<?>) {
            return net.sf.json.JSONArray.fromObject(obj).toString();
        } else if (obj instanceof Boolean) {
            Boolean boolObj = (Boolean)obj;
            return boolObj.toString();
        } else if (obj instanceof PageEditMode) {
            PageEditMode pageEditModeObj = (PageEditMode)obj;
            return pageEditModeObj.name();
        } else if (obj.getClass().isArray()) {
            //have not confirmed that this works
            return net.sf.json.JSONArray.fromObject(obj).toString();
        } else {
            //have not confirmed that this works
            return net.sf.json.JSONObject.fromObject(obj).toString();
        }
    }
    
    /**
     * We want to return a string that doesn't contain any new lines,
     * quote characters, or other nonsense.
     * 
     * The returned string should be able to be placed within quotes
     * to form a JavaScript string literal.
     * 
     * @param obj
     * @return
     */
    public static String generateSafeJsString(Object obj) {
        String str = obj.toString();
        str = str.replaceAll("[^\\w]+", " ");
        return str;
    }
    
    /**
     * Method which escapes the characters in a String using JavaScript String
     * rules
     * @param string - String to escape
     * @return The escaped string
     */
    public static String escapeJavaScript(String string) {
        return StringEscapeUtils.escapeJavaScript(string);
    }
}
