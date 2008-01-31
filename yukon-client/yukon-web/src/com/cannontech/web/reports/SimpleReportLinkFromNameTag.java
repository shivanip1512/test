package com.cannontech.web.reports;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable("simpleReportLinkFromNameTagPrototype")
public class SimpleReportLinkFromNameTag extends SimpleReportLinkFromNameTagBase implements DynamicAttributes {
    
    private String definitionName;
    private String viewType;
    
    private Map<String, Object> identifierAttributes = new HashMap<String, Object>();
    
    @Override
    public void doTag() throws JspException, IOException {
        
        JspWriter out = getJspContext().getOut();
        PageContext context = (PageContext)getJspContext();
        HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest();
        
        Map<String, String> propertiesMap = getpropertiesMap(definitionName, identifierAttributes);
        
        String url = buildUrl(viewType, propertiesMap, true, httpRequest);
        
        // construct final <a> tag
        out.print("<a href=\"" + url + "\">");
        getJspBody().invoke(out);
        out.print("</a>");
        
    }
    
    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }
    
    public void setViewType(String viewer) {
        this.viewType = viewer;
    }
    
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        identifierAttributes.put(localName, value);
    }

}
