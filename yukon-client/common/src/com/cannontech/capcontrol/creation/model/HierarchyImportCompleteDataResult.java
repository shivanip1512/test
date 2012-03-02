package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class HierarchyImportCompleteDataResult implements HierarchyImportResult {
    private HierarchyImportData hierarchyImportData;
    private HierarchyImportResultType resultType;
    
    public HierarchyImportCompleteDataResult(HierarchyImportData hierarchyImportData, HierarchyImportResultType resultType) {
        // The user should be using HierarchyImportMissingDataResult instead if this is null...
        Validate.notNull(hierarchyImportData);
        this.hierarchyImportData = hierarchyImportData;
        this.resultType = resultType;
    }
    
    public HierarchyImportData getHierarchyImportData() {
        return hierarchyImportData;
    }
    
    @Override
    public Element getResponseElement(Namespace ns) {
        Element response = new Element("hierarchyImportResponse", ns);
        
        if (!resultType.isSuccess()) {
            Element cbcResult = new Element("hierarchyImportResultType", ns);
            cbcResult.addContent(resultType.getDbString());
            response.addContent(cbcResult);
        }
        
        ImportAction action = hierarchyImportData.getImportAction();
        String name = hierarchyImportData.getName();
        String outcome = resultType.isSuccess() ? "succeeded" : "failed";
        String actionString = (action != null) ? action.getDbString() : "Import"; 
        
        String detail = actionString + " " + outcome + " for object with name \"" + name + "\".";
        
        Element detailElement = new Element("resultDetail", ns);
        detailElement.addContent(detail);
        
        response.addContent(detailElement);
        
        return response;
    }

    private String getResultKey(ImportAction action, boolean success) {
        String key = (action != null) ? action.getDbString() : "Import";
        key += success ? "Success" : "Failure";
        
        return key;
    }
    
    @Override
    public YukonMessageSourceResolvable getMessage() {
        String key = "yukon.web.modules.capcontrol.import.hierarchy";
        
        key += getResultKey(hierarchyImportData.getImportAction(), resultType.isSuccess());
        
        return new YukonMessageSourceResolvable(key, resultType.getDbString(), hierarchyImportData.getName());
    }

    @Override
    public HierarchyImportResultType getResultType() {
        return resultType;
    }

}
