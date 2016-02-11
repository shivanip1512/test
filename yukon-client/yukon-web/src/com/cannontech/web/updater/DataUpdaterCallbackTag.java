package com.cannontech.web.updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.JavaScriptUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.updater.capcontrol.exception.CacheManagementException;

@Configurable("dataUpdaterCallbackTagPrototype")
public class DataUpdaterCallbackTag extends YukonTagSupport implements DynamicAttributes {
    private String function;
    private boolean initialize = false;
    private DataUpdaterService dataUpdaterService;
    private Map<String,Object> identifierAttributes = new HashMap<String,Object>();
    private static final Logger log = YukonLogManager.getLogger(DataUpdaterCallbackTag.class);
    
    @Override
    public void doTag() throws JspException, IOException {
        
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
            
            Map<String,String> identifierValues = new HashMap<String, String>();
            for(String identifierName : identifierAttributes.keySet()) {
                String fullIdentifier = (String) identifierAttributes.get(identifierName);
                UpdateValue identifierValue = null;
                try {
                    identifierValue = dataUpdaterService.getFirstValue(fullIdentifier, getUserContext());
                } catch (CacheManagementException cme) {
                    log.debug("Unable to get the identifierValue");
                }
                if (identifierValue != null && !identifierValue.isUnavailable()) {
                    identifierValues.put(identifierName, identifierValue.getValue());
                }
            }
            
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
