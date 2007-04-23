package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class UserPickerTag extends ItemPickerTag {
    public UserPickerTag() {
        super();
    }
    
    public void doTag() throws JspException, IOException {
        startOfTag("/JavaScript/userPicker.js");
        
        // build up extraMapping string
        List<String> extraMappings = new ArrayList<String>();
        if (StringUtils.isNotBlank(itemNameElement)) {
            extraMappings.add("userName:" + itemNameElement);
        }
        if (StringUtils.isNotBlank(parentItemNameElement)) {
            extraMappings.add("loginGroupName:" + parentItemNameElement);
        }
        String extraMappingString = StringUtils.join(extraMappings.iterator(), ";");
        
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String initString = "\'"
            + itemIdField + "\','" + constraint + "','" 
            + extraMappingString + "','" + pickerId + "','"
            + request.getContextPath() + "'," + finalTriggerAction;
        getJspContext().getOut().println("<script> var " + pickerId + " = new UserPicker(" + initString + ");");
        getJspContext().getOut().println("</script> ");
        String outputTagString = "<a href=\"javascript:" + pickerId + ".showPicker()\">";
        getJspContext().getOut().println( outputTagString );
        getJspBody().invoke(null);
        getJspContext().getOut().println("</a>");
    }
    
    public String getUserIdField() {
        return itemIdField;
    }

    public void setUserIdField(String itemIdField) {
        this.itemIdField = itemIdField;
    }

    public String getUserNameElement() {
        return itemNameElement;
    }

    public void setUserNameElement(String itemNameElement) {
        this.itemNameElement = itemNameElement;
    }
}
