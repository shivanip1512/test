package com.cannontech.web.taglib;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.JsonUtils;

public class YukonCookieTag extends YukonTagSupport {
    
    private String var;
    private String scope;
    private String id;
    private String defaultValue = "";

    @SuppressWarnings("unchecked")
    @Override
    public void doTag() throws IOException {
        
        Object result = defaultValue;
        
        Map<String, Cookie> cookies = new HashMap<String, Cookie>();
        for (Cookie cookie : getRequest().getCookies()) cookies.put(cookie.getName(), cookie);
        
        Cookie yukon = cookies.get("yukon");
        
        if (yukon != null) {
            String json = yukon.getValue();
            String cookieText = URLDecoder.decode(json, "UTF-8");
            Map<String, Object> data = JsonUtils.fromJson(cookieText, Map.class);
            
            Object value = data.get(scope + id);
            if (value != null) result = value;
        }
        
        if (StringUtils.isNotBlank(var)) {
            getJspContext().setAttribute(var, result);
        } else {
            getJspContext().getOut().print(result);
        }
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
}