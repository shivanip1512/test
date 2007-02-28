package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class PointPickerTag extends ItemPickerTag {
    public PointPickerTag() {
        super();
    }
    
    public void doTag() throws JspException, IOException {
        startOfTag("/JavaScript/pointPicker.js");
        
        // build up extraMapping string
        List<String> extraMappings = new ArrayList<String>();
        if (StringUtils.isNotBlank(itemNameElement)) {
            extraMappings.add("pointName:" + itemNameElement);
        }
        if (StringUtils.isNotBlank(parentItemNameElement)) {
            extraMappings.add("deviceName:" + parentItemNameElement);
        }
        String extraMappingString = StringUtils.join(extraMappings.iterator(), ";");
        
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String initString = "\'"
            + itemIdField + "\','" + constraint + "','" 
            + extraMappingString + "','" + pickerId + "','"
            + request.getContextPath() + "'";
        getJspContext().getOut().println("<script> var " + pickerId + " = new PointPicker(" + initString + ");");
        getJspContext().getOut().println("</script> ");
        String outputTagString = "<a href=\"javascript:" + pickerId + ".showPicker()\">";
        getJspContext().getOut().println( outputTagString );
        getJspBody().invoke(null);
        getJspContext().getOut().println("</a>");
    }
    
    public String getPointIdField() {
        return itemIdField;
    }

    public void setPointIdField(String itemIdField) {
        this.itemIdField = itemIdField;
    }

    public String getPointNameElement() {
        return itemNameElement;
    }

    public void setPointNameElement(String itemNameElement) {
        this.itemNameElement = itemNameElement;
    }

    public String getDeviceNameElement() {
        return parentItemNameElement;
    }

    public void setDeviceNameElement(String parentItemNameElement) {
        this.parentItemNameElement = parentItemNameElement;
    }
}
