package com.cannontech.web.reports;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable("simpleReportLinkFromNameTagPrototype")
public class SimpleReportLinkFromNameTag extends SimpleReportLinkFromNameTagBase {
    
    private String definitionName;
    private String viewType;
    
    @Override
    public void doTag() throws JspException, IOException {
        
    	setContext();
    	
        Map<String, String> propertiesMap = getpropertiesMap(definitionName);
        String url = buildUrl(viewType, propertiesMap, true);
        
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
    
}
