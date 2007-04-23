package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class LoginGroupPickerTag extends ItemPickerTag {
    public LoginGroupPickerTag() {
        super();
    }
    
    public void doTag() throws JspException, IOException {
        startOfTag("/JavaScript/loginGroupPicker.js");
          
        // build up extraMapping string
        List<String> extraMappings = new ArrayList<String>();
        if (StringUtils.isNotBlank(itemNameElement)) {
            extraMappings.add("groupName:" + itemNameElement);
        }
        String extraMappingString = StringUtils.join(extraMappings.iterator(), ";");
        
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String initString = "\'"
            + itemIdField + "\','" + constraint + "','" 
            + extraMappingString + "','" + pickerId + "','"
            + request.getContextPath() + "'," + finalTriggerAction;
        getJspContext().getOut().println("<script> var " + pickerId + " = new LoginGroupPicker(" + initString + ");");
        getJspContext().getOut().println("</script> ");
        String outputTagString = "<a href=\"javascript:" + pickerId + ".showPicker()\">";
        getJspContext().getOut().println( outputTagString );
        getJspBody().invoke(null);
        getJspContext().getOut().println("</a>");
    }
    
    public String getLoginGroupIdField() {
        return itemIdField;
    }

    public void setLoginGroupIdField(String itemIdField) {
        this.itemIdField = itemIdField;
    }

    public String getLoginGroupNameElement() {
        return itemNameElement;
    }

    public void setLoginGroupNameElement(String itemNameElement) {
        this.itemNameElement = itemNameElement;
    }
}
