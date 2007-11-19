package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ItemPickerTag extends SimpleTagSupport {
    protected String pickerId = null; 
    protected String itemIdField;
    protected String constraint;
    protected String itemNameElement;
    protected String parentItemNameElement;
    protected String finalTriggerAction;

    public ItemPickerTag() {
        super();
    }
    
    public void startOfTag(String... newPickerJsFiles) {
        // make sure our script file is included
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (spTag != null) {
            spTag.addScriptFile("/JavaScript/itemPicker.js");
            for(String jsFile : newPickerJsFiles) {
                spTag.addScriptFile(jsFile);
            }
            spTag.addScriptFile("/JavaScript/tableCreation.js");
            spTag.addCSSFile("/WebConfig/yukon/styles/itemPicker.css");
        }
        
        // if pickerId is null, pick random
        if (pickerId == null) {
            pickerId = UniqueIdentifierTag.generateIdentifier(getJspContext(), "picker_");
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

    public String getFinalTriggerAction() {
        return finalTriggerAction;
    }

    public void setFinalTriggerAction(String finalTriggerAction) {
        this.finalTriggerAction = finalTriggerAction;
    }

}
