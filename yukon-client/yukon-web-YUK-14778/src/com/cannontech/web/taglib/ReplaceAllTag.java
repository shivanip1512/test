package com.cannontech.web.taglib;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class ReplaceAllTag extends YukonTagSupport {
    
    private String var;
    private String input;
    private String pattern;
    private String replace;

    @Override
    public void doTag() throws IOException {
        
        String result = input.replaceAll(pattern, replace);
        
        if (StringUtils.isNotBlank(var)) {
            getJspContext().setAttribute(var, result);
        } else {
            getJspContext().getOut().print(result);
        }
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setInput(String input) {
        this.input = input;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public void setReplace(String replace) {
        this.replace = replace;
    }
    
}