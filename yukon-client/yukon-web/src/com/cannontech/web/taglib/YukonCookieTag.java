package com.cannontech.web.taglib;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.util.JsonUtils;

public class YukonCookieTag extends YukonTagSupport {
    
    private String var;
    private String scope;
    private String id;
    private String defaultValue = "";

    @SuppressWarnings("unchecked")
    @Override
    public void doTag() throws IOException {
        
        Map<String, Cookie> cookies = new HashMap<String, Cookie>();
        for (Cookie cookie : getRequest().getCookies()) cookies.put(cookie.getName(), cookie);
        
        String cookieText = URLDecoder.decode(cookies.get("yukon").getValue(), "UTF-8");
        Map<String, String> cookie = JsonUtils.fromJson(cookieText, Map.class);
        
        String value = cookie.get(scope + id);
        
        if (value == null) value = defaultValue;
        
        if (StringUtils.isNotBlank(var)) {
            getJspContext().setAttribute(var, value);
        } else {
            getJspContext().getOut().print(value);
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