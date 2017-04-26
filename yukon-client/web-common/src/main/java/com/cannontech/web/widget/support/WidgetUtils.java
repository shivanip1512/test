package com.cannontech.web.widget.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WidgetUtils {
    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

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

    public static String generateJsonString(Object obj) throws JsonProcessingException {
        return jsonObjectMapper.writeValueAsString(obj);
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
        return StringEscapeUtils.escapeEcmaScript(string);
    }

    /**
     * Method which joins elements of an Iterable into a string separating each element with a given
     * separator.
     * 
     * @param elements - List of elements
     * @param separator - Separator to be used
     * @return The string containing the list of elements separated using the separator passed as an argument
     */
    public static String join(Iterable<?> elements, CharSequence separator) {
        StringBuilder buf = new StringBuilder();
        if (elements != null) {
            if (separator == null)
                separator = " ";
            for (Object o : elements) {
                if (buf.length() > 0)
                    buf.append(separator);
                buf.append(o);
            }
        }
        return buf.toString();
    }
}
