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

@Configurable("simpleReportUrlFromNameTagPrototype")
public class SimpleReportUrlFromNameTag extends SimpleReportLinkFromNameTagBase implements DynamicAttributes {
    
    private String definitionName;
    private String viewType;
    private String var;
    private Boolean htmlOutput;
    
    private Map<String, Object> identifierAttributes = new HashMap<String, Object>();
    
    @Override
    public void doTag() throws JspException, IOException {
        
        JspWriter out = getJspContext().getOut();
        PageContext context = (PageContext)getJspContext();
        HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest();
        
        Map<String, String> propertiesMap = getpropertiesMap(definitionName, identifierAttributes);
        
        String url = buildUrl(viewType, propertiesMap, htmlOutput, httpRequest);
        
        // write url to page or place in page context
        if (var == null) {
            out.print(url);
        } else {
            context.setAttribute(var, url);
        }
        
    }
    
    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }
    
    public void setViewType(String viewer) {
        this.viewType = viewer;
    }
    
    public void setVar(String var) {
        this.var = var;
    }

    public void setHtmlOutput(Boolean htmlOutput) {
        this.htmlOutput = htmlOutput;
    }
    
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        identifierAttributes.put(localName, value);
    }

}
