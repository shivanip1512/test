package com.cannontech.web.updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("dataUpdaterCallbackTagPrototype")
public class DataUpdaterCallbackTag extends YukonTagSupport implements DynamicAttributes {
    private String function;
    private DataUpdaterService dataUpdaterService;
    private Map<String,Object> identifierAttributes = new HashMap<String,Object>();
    
    @Override
    public void doTag() throws JspException, IOException {
        
        LiteYukonUser user = getYukonUser();
        Map<String,String> identifierValues = new HashMap<String, String>();
        for(String identifierName : identifierAttributes.keySet()) {
            UpdateValue identifierValue = dataUpdaterService.getFirstValue((String)identifierAttributes.get(identifierName), user);
            identifierValues.put(identifierName, identifierValue.getValue());
        }
        
        JspWriter out = getJspContext().getOut();
        out.print("<script type=\"text/javascript\">");
        
        out.print("\n\tcannonDataUpdateRegistration(" + function + ",{");
        boolean firstId = true;
        for(String identifierName : identifierAttributes.keySet()) {
            
            if(!firstId) {
                out.print(",");
            }
            out.print("'" + identifierName + "':'" + identifierAttributes.get(identifierName) + "'");
            firstId = false;
        }
        out.print("});");
        
        out.print("\n\n</script>");
    }
    
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        identifierAttributes.put(localName, value);
    }
    
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    
    public void setRegistrationService(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }

}
