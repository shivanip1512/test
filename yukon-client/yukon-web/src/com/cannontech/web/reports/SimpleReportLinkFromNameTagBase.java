package com.cannontech.web.reports;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.YukonReportDefinition;
import com.cannontech.simplereport.YukonReportDefinitionFactory;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.taglib.YukonTagSupport;

public abstract class SimpleReportLinkFromNameTagBase extends YukonTagSupport{
    
    private YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory;
    private SimpleReportService simpleReportService;
    
    
    
    /**
     * Method to override to provide specifc output of simple report url.
     * Overrides SimpleTagSupport method.
     */
    @Override
    public abstract void doTag() throws JspException, IOException;
     
    /**
     * Build a parameter map from dynamic tag attributes that are required by report definition.
     * Add additional parameters that are typical defaults for a simple report url.
     * @param definitionName
     * @param identifierAttributes
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getpropertiesMap(String definitionName, Map<String, Object> identifierAttributes) { 
    
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
        optionalAttributeDefaults.put("viewJsp", "MENU");
        
        CtiUtilities.overrideValuesOfDefaultsMap(optionalAttributeDefaults, identifierAttributes);
        
        propertiesMap.putAll(optionalAttributeDefaults);
        propertiesMap.put("def", definitionName);
        
        return propertiesMap;
        
    }

    /**
     * Build a safe simple report url given a map of query parameters.
     * 
     * @param viewType
     * @param propertiesMap
     * @param htmlOutput
     * @return
     * @throws IOException
     */
    protected String buildUrl(String viewType, Map<String, String> propertiesMap, Boolean htmlOutput, HttpServletRequest httpRequest) throws IOException{
        
        // build safe URL query string
        String queryString = ServletUtil.buildSafeQueryStringFromMap(propertiesMap, htmlOutput);
        
        // complete URL
        String url = "/spring/reports/simple/" + viewType + "?" + queryString;
        url = ServletUtil.createSafeUrl(httpRequest, url);
        
        return url;
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

}
