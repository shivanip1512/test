package com.cannontech.common.util;

import java.util.HashMap;
import java.util.Map;

public class ResolvableTemplate {
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
