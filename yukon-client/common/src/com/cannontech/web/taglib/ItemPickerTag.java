package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

public class ItemPickerTag extends SimpleTagSupport {
    protected String pickerId; 
    protected String itemIdField;
    protected String constraint;
    protected String itemNameElement;
    protected String parentItemNameElement;

    public ItemPickerTag() {
        super();
    }
    
    public void startOfTag(String newPickerJsFile) {
        // make sure our script file is included
        StandardPageTag spTag = (StandardPageTag) findAncestorWithClass(this, StandardPageTag.class);
        if (spTag != null) {
            spTag.addScriptFile("/JavaScript/itemPicker.js");
            spTag.addScriptFile(newPickerJsFile);
            spTag.addScriptFile("/JavaScript/tableCreation.js");
            spTag.addCSSFile("/WebConfig/yukon/styles/itemPicker.css");
        }
    }
    
    public void doTag() throws JspException, IOException {
    }
    
    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getItemIdField() {
        return itemIdField;
    }

    public void setItemIdField(String itemIdField) {
        this.itemIdField = itemIdField;
    }

    public String getItemNameElement() {
        return itemNameElement;
    }

    public void setItemNameElement(String itemNameElement) {
        this.itemNameElement = itemNameElement;
    }

    public String getParentItemNameElement() {
        return parentItemNameElement;
    }

    public void setParentItemNameElement(String parentItemNameElement) {
        this.parentItemNameElement = parentItemNameElement;
    }

    public String getPickerId() {
        return pickerId;
    }

    public void setPickerId(String pickerId) {
        this.pickerId = pickerId;
    }

}
