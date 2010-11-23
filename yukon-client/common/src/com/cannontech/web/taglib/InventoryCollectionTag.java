package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;

public class InventoryCollectionTag extends SimpleTagSupport {

    InventoryCollection inventoryCollection = null;

    @Override
    public void doTag() throws JspException, IOException {

        JspWriter out = getJspContext().getOut();

        // Write out all of the parameters as hidden fields
        if (inventoryCollection != null) {
            Map<String, String> collectionParameters = inventoryCollection.getCollectionParameters();
            for (String name : collectionParameters.keySet()) {
                String value = collectionParameters.get(name);
                out.write("<input type=\"hidden\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeXml(value) + "\" >");
            }
        }

    }
    
    public void setInventoryCollection(InventoryCollection inventoryCollection) {
        this.inventoryCollection = inventoryCollection;
    }
    
}