package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;

public class DatabaseMigrationPickerTag extends MultiPaoPickerTag {

    private String imgUrl = null;
    private String configurationName = null;
    
    public DatabaseMigrationPickerTag() {
        super();
    }

    public void doTag() throws JspException, IOException {
        startOfTag("/JavaScript/paoPicker.js", "/JavaScript/multiPaoPicker.js", "/JavaScript/databaseMigrationPicker.js");

        String extraMappingString = "";

        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String initString = "\'" + itemIdField + "\','" + constraint + "','" + extraMappingString + "','" + pickerId + "','" + request.getContextPath() + "'," + finalTriggerAction + ",'"+configurationName+"'" ;
        getJspContext().getOut().println("<script type=\"text/javascript\"> " + pickerId + " = new DatabaseMigrationPicker(" + initString + ");");
        getJspContext().getOut().println("</script> ");
        
        if (imgUrl != null){
            String outputTagString = "";
            outputTagString = "<img src=\""+imgUrl+"\" value=\"";
            getJspContext().getOut().print(outputTagString);
            
            outputTagString = "\" onclick=\"javascript:" + pickerId + ".showPicker()\" >";
            getJspContext().getOut().println(outputTagString);
        }
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

    public String getImgUrl() {
        return imgUrl;
    }
    
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getConfigurationName() {
        return configurationName;
    }
    
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }
}


