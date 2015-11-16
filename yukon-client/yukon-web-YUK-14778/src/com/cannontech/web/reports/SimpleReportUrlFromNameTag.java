package com.cannontech.web.reports;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable("simpleReportUrlFromNameTagPrototype")
public class SimpleReportUrlFromNameTag extends SimpleReportLinkFromNameTagBase {
    
    private String definitionName;
    private String viewType;
    private String var;
    private Boolean htmlOutput;
    
    @Override
    public void doTag() throws JspException, IOException {
        
    	setContext();
    	
        Map<String, String> propertiesMap = getpropertiesMap(definitionName);
        String url = buildUrl(viewType, propertiesMap, htmlOutput);
        
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
    
}
