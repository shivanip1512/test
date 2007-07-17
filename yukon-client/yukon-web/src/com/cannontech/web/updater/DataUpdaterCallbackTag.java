package com.cannontech.web.updater;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("dataUpdaterCallbackTagPrototype")
public class DataUpdaterCallbackTag extends YukonTagSupport {
    private String function;
    private String identifier;
    private DataUpdaterService dataUpdaterService;

    
    @Override
    public void doTag() throws JspException, IOException {
        UpdateValue value = dataUpdaterService.getFirstValue(identifier, getYukonUser());
        
        JspWriter out = getJspContext().getOut();
        out.print("<script type=\"text/javascript\">");
        out.print("  ");
        out.print(function);
        out.print("('" + value.getValue() + "');\n");
        out.print("  cannonDataUpdateRegistration");
        out.print("('" + identifier + "', " + function + ");\n");
        out.print("</script>");
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public void setRegistrationService(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }

}
