package com.cannontech.web.i18n;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.web.taglib.YukonTagSupport;

public abstract class HtmlTagBase extends YukonTagSupport implements DynamicAttributes {
    private Map<String, String> linkAttributes = new HashMap<String, String>();
    private String key;


    public void setKey(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    
    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        linkAttributes.put(localName, value.toString());
    }
    
    protected void outputBaseAttributes(Writer out) throws JspException, IOException {
        Map<String, String> attributes = new HashMap<String, String>();
        for (String attribute : getSupportedAttributes()) {
            String tagValue = linkAttributes.remove(attribute);
            String attributeValue = getAttributeValue(attribute, tagValue);
            if (StringUtils.isNotBlank(attributeValue)) {
                attributeValue = StringEscapeUtils.escapeHtml4(attributeValue);
                attributes.put(attribute, attributeValue);
            }
        }
        if (!linkAttributes.isEmpty()) {
            throw new JspException("Found unsupported attributes on tag: " + linkAttributes);
        }
        
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            out.write(entry.getKey());
            out.write("=\"");
            out.write(entry.getValue());
            out.write("\" ");
        }


    }
    
    protected abstract Set<String> getSupportedAttributes();
    
    protected String getAttributeValue(String attributeName, String attributeTagValue) {
        String attributeValue = null;
        // see if we have a key
        String attributeKeyName = getKey() + "." + attributeName;
        try {
            attributeValue = getMessageSource().getMessage(attributeKeyName, attributeTagValue);
        } catch (NoSuchMessageException e) {
            attributeValue = attributeTagValue;
        }
        return attributeValue;
    }

}
