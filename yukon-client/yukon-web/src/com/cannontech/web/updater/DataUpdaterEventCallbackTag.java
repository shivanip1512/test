package com.cannontech.web.updater;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("dataUpdaterEventCallbackTagPrototype")
public class DataUpdaterEventCallbackTag extends YukonTagSupport {
    private String function;
    private String id;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        JspWriter out = getJspContext().getOut();
        out.print("<script>");
        out.print("\nyukon.dataUpdater.registerEventCallback(" + function + ", \"" + id + "\");\n");
        out.print("\n</script>");
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    
}