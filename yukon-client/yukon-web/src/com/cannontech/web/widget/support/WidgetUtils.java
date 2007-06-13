package com.cannontech.web.widget.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class WidgetUtils {
    
    public static Map<String,String> combineParameters(Map<String,String> outer, Map<String,? extends Object> inner) {
        Map<String,String> emptyMap1 = Collections.emptyMap();
        outer = (outer != null ? outer : emptyMap1);
        Map<String,Object> emptyMap2 = Collections.emptyMap();
        inner = (inner != null ? inner : emptyMap2);
        Map<String,String> result = Collections.checkedMap(new HashMap<String,String>(), String.class, String.class);
        result.putAll(outer);
        for (Map.Entry<String,? extends Object> entry : inner.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }
        return result;
    }
    
    public static String generateJsonString(Map obj) {
        return new JSONObject(obj).toString();
    }
}
