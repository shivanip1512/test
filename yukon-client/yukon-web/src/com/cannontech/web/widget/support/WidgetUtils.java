package com.cannontech.web.widget.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class WidgetUtils {
    
    public static <K,V> Map<K,V> combineParameters(Map<K,V> outer, Map<K,V> inner) {
        Map<K,V> emptyMap = Collections.emptyMap();
        outer = (outer != null ? outer : emptyMap);
        inner = (inner != null ? inner : emptyMap);
        Map<K,V> result = new HashMap<K,V>(outer);
        for (Map.Entry<K,V> entry : inner.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    public static String generateJsonString(Map obj) {
        return new JSONObject(obj).toString();
    }
}
