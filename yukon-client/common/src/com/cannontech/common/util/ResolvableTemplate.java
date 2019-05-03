package com.cannontech.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a holder for data to be processed by a FormattingTemplateProcessor.
 * In a JSP page, it can be rendered with the FormatTemplateTag.
 */
public class ResolvableTemplate implements Serializable {
    private String code = null;
    private Map<String, Object> data = new HashMap<String, Object>();
    
    public ResolvableTemplate(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String key) {
        this.code = key;
    }
    
    public void addData(String key, Object value) {
        data.put(key, value);
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

}
