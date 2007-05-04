package com.cannontech.common.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class SimpleTemplateProcessor {
    public static String process(CharSequence template, Map<String, ? extends Object> values) {
        String templateStr = template.toString();
        for (Map.Entry<String, ? extends Object> entry : values.entrySet()) {
            String rawKey = entry.getKey();
            Validate.isTrue(StringUtils.isAlphanumeric(rawKey), "Key's must only contain letters and numbers");
            String key = "{" + rawKey + "}";
            templateStr = templateStr.replace(key, entry.getValue().toString());
        }
        return templateStr;
    }
    
    public static boolean contains(CharSequence template, String key) {
        String searchKey = "{" + key + "}";
        boolean result = template.toString().contains(searchKey);
        return result;
    }
}
