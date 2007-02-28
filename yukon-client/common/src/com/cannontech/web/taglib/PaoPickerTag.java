package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class PaoPickerTag extends ItemPickerTag {
    public PaoPickerTag() {
        super();
    }
    
    public void doTag() throws JspException, IOException {
        startOfTag("/JavaScript/paoPicker.js");
        
        // build up extraMapping string
        List<String> extraMappings = new ArrayList<String>();
        if (StringUtils.isNotBlank(itemNameElement)) {
            extraMappings.add("paoName:" + itemNameElement);
        }
        if (StringUtils.isNotBlank(parentItemNameElement)) {
            extraMappings.add("type:" + parentItemNameElement);
        }
        String extraMappingString = StringUtils.join(extraMappings.iterator(), ";");
        
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String initString = "\'"
            + itemIdField + "\','" + constraint + "','" 
            + extraMappingString + "','" + pickerId + "','"
            + request.getContextPath() + "'";
        getJspContext().getOut().println("<script> var " + pickerId + " = new PaoPicker(" + initString + ");");
        getJspContext().getOut().println("</script> ");
        String outputTagString = "<a href=\"javascript:" + pickerId + ".showPicker()\">";
        getJspContext().getOut().println( outputTagString );
        getJspBody().invoke(null);
        getJspContext().getOut().println("</a>");
    }
    
    public String getPaoIdField() {
        return itemIdField;
    }

    public void setPaoIdField(String itemIdField) {
        this.itemIdField = itemIdField;
    }

    public String getPaoNameElement() {
        return itemNameElement;
    }

    public void setPaoNameElement(String itemNameElement) {
        this.itemNameElement = itemNameElement;
    }

    public String getTypeElement() {
        return parentItemNameElement;
    }

    public void setTypeElement(String parentItemNameElement) {
        this.parentItemNameElement = parentItemNameElement;
    }
}
