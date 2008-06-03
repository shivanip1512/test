package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.updater.DataUpdaterService;
import com.cannontech.web.updater.UpdateValue;

@Configurable("dataUpdaterValueTagPrototype")
public class DataUpdaterValueTag extends YukonTagSupport {
    private DataUpdaterService dataUpdaterService;
    private String type;
    private String identifier;
    private String styleClass = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        UpdateValue value = dataUpdaterService.getFirstValue(getType() + "/" + getIdentifier(), getUserContext());
        
        JspWriter out = getJspContext().getOut();
        out.print("<span cannonUpdater=\"" + value.getFullIdentifier() + "\" class=\"" + styleClass + "\" >");
        out.print(value.getValue());
        out.print("</span>");
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setDataUpdaterService(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }

}
