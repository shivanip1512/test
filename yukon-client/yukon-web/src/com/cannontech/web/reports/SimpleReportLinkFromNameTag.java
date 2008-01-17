package com.cannontech.web.reports;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.YukonReportDefinition;
import com.cannontech.simplereport.YukonReportDefinitionFactory;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("simpleReportLinkFromNameTagPrototype")
public class SimpleReportLinkFromNameTag extends YukonTagSupport implements DynamicAttributes {
    
    private String definitionName;
    private String viewType;
    private YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory;
    private SimpleReportService simpleReportService;
    
    private Map<String, Object> identifierAttributes = new HashMap<String, Object>();
    
    
    @Override
    public void doTag() throws JspException, IOException {
        
        // access to page writer and request
        JspWriter out = getJspContext().getOut();
        PageContext context = (PageContext)getJspContext();
        HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest();
        
        // get definitionName, model, input root
        YukonReportDefinition<BareReportModel> reportDefinition = reportDefinitionFactory.getReportDefinition(definitionName);
        InputRoot inputRoot = reportDefinition.getInputs();
        
        // extract properties from dynamic attributes
        Map<String,String> propertiesMap = null;
        if(identifierAttributes.containsKey("parameterAttributes")) {
            Map<String, Object> parameterAttributes = (Map<String, Object>)identifierAttributes.get("parameterAttributes");
            propertiesMap = simpleReportService.extractPropertiesFromAttributesMap(inputRoot, parameterAttributes);
        }
        else {
            propertiesMap = simpleReportService.extractPropertiesFromAttributesMap(inputRoot, identifierAttributes);
        }
        
        
        // other optional attributes
        Map<String, String> optionalAttributeDefaults = new HashMap<String, String>();
        optionalAttributeDefaults.put("module", "blank");
        optionalAttributeDefaults.put("showMenu", "false");
        optionalAttributeDefaults.put("menuSelection", "");
        CtiUtilities.overrideValuesOfDefaultsMap(optionalAttributeDefaults, identifierAttributes);
        
        propertiesMap.putAll(optionalAttributeDefaults);
        
        // build safe URL query string
        String queryString = ServletUtil.buildSafeQueryStringFromMap(propertiesMap);
        
        // complete URL
        String url = "/spring/reports/simple/" + viewType + "?def=" + definitionName + queryString;
        url = ServletUtil.createSafeUrl(httpRequest, url);
        url = StringEscapeUtils.escapeHtml(url);
        
        // construct final <a> tag
        out.print("<a href=\"" + url + "\">");
        getJspBody().invoke(out);
        out.print("</a>");
        
    }
    
    
    
    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }
    
    @Required
    public void setReportDefinitionFactory(
            YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory) {
        this.reportDefinitionFactory = reportDefinitionFactory;
    }

    @Required
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }  
    
    public void setViewType(String viewer) {
        this.viewType = viewer;
    }
    
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        identifierAttributes.put(localName, value);
    }


      
}
