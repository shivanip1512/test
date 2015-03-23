package com.cannontech.web.updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.JavaScriptUtils;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("dataUpdaterCallbackTagPrototype")
public class DataUpdaterCallbackTag extends YukonTagSupport implements DynamicAttributes {
    private String function;
    private boolean initialize = false;
    private DataUpdaterService dataUpdaterService;
    private Map<String,Object> identifierAttributes = new HashMap<String,Object>();
    
    @Override
    public void doTag() throws JspException, IOException {
        
        Map<String,String> identifierValues = new HashMap<String, String>();
        for(String identifierName : identifierAttributes.keySet()) {
            UpdateValue identifierValue = dataUpdaterService.getFirstValue((String)identifierAttributes.get(identifierName), getUserContext());
            if (!identifierValue.isUnavailable()) {
                identifierValues.put(identifierName, identifierValue.getValue());
            }
        }
        
        JspWriter out = getJspContext().getOut();
        out.print("<script>");
        
        // print out a call to register the callback function
        out.print("\nyukon.dataUpdater.registerCallback(" + function + ",{");
        boolean firstId = true;
        for(String identifierName : identifierAttributes.keySet()) {
            
            if(!firstId) {
                out.print(",");
            }
            out.print("'" + identifierName + "':'" + identifierAttributes.get(identifierName) + "'");
            firstId = false;
        }
        out.print("});\n");
        
        if (initialize) {
            // now actually print out a call to the function
            out.print(function + "({");

            firstId = true;
            for(String identifierName : identifierAttributes.keySet()) {
                if (identifierValues.containsKey(identifierName)) {
                    if(!firstId) {
                        out.print(",");
                    }
                    String value = identifierValues.get(identifierName);
                    if (value == null) {
                        value = "";
                    }
                    out.print("'");
                    out.print(identifierName);
                    out.print("':'");
                    out.print(JavaScriptUtils.javaScriptEscape(value));
                    out.print("'");
                    firstId = false;
                }
            }
            out.print("});");
        }
        
        out.print("\n</script>");
    }
    
    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        identifierAttributes.put(localName, value);
    }
    
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    
    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }
    
    public void setRegistrationService(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }

}
