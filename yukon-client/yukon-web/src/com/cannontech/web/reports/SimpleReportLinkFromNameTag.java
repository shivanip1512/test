package com.cannontech.web.reports;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.util.ServletUtil;

@Configurable("simpleReportLinkFromNameTagPrototype")
public class SimpleReportLinkFromNameTag extends SimpleReportLinkFromNameTagBase {
    
    private String definitionName;
    private String viewType;
    private String var;
    private int scope = PageContext.PAGE_SCOPE;
    
    @Override
    public void doTag() throws JspException, IOException {
        
    	setContext();
    	
        Map<String, String> propertiesMap = getpropertiesMap(definitionName);
        String url = buildUrl(viewType, propertiesMap, true);
        url = ServletUtil.createSafeUrl(getRequest(), url);
        
        if (var == null) {
         // construct final <a> tag
            out.print("<a href=\"" + url + "\">");
            getJspBody().invoke(out);
            out.print("</a>");
        } else {
            getPageContext().setAttribute(var, url, scope);
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
    
}