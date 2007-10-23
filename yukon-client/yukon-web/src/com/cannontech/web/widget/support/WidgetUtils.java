package com.cannontech.web.widget.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

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
        if (obj instanceof String) {
            return "\"" + StringEscapeUtils.escapeJavaScript((String) obj) + "\"";
        } else if (obj instanceof Map) {
            return new JSONObject((Map)obj).toString();
        } else {
            return JSONObject.fromObject(obj).toString();
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
}
