package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import com.cannontech.common.bulk.collection.inventory.YukonCollection;

public class InventoryCollectionTag extends SimpleTagSupport {

    YukonCollection yukonCollection = null;

    @Override
    public void doTag() throws JspException, IOException {

        JspWriter out = getJspContext().getOut();

        // Write out all of the parameters as hidden fields
        if (yukonCollection != null) {
            Map<String, String> collectionParameters = yukonCollection.getCollectionParameters();
            for (String name : collectionParameters.keySet()) {
                String value = collectionParameters.get(name);
                out.write("<input type=\"hidden\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeXml(value) + "\" >");
            }
        }

    }
    
    public void setInventoryCollection(YukonCollection yukonCollection) {
        this.yukonCollection = yukonCollection;
    }
    
}