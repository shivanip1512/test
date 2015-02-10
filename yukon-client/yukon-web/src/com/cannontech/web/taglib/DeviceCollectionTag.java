package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;

/**
 * Tag used to output the hidden fields neccessary to pass a device collection
 * through a page
 */
public class DeviceCollectionTag extends SimpleTagSupport {
    
    DeviceCollection deviceCollection = null;
    
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        
        JspWriter out = getJspContext().getOut();
        
        // Write out all of the parameters as hidden fields
        if (deviceCollection != null) {
            Map<String, String> collectionParameters = deviceCollection.getCollectionParameters();
            for (String name : collectionParameters.keySet()) {
                String value = collectionParameters.get(name);
                out.write("<input type=\"hidden\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeXml10(value) + "\" >");
            }
        }
        
    }
}