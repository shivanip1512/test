package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang3.Validate;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class CbcImportCompleteDataResult implements CbcImportResult {
    private final CbcImportData cbcImportData;
    private final CbcImportResultType resultType;

    public CbcImportCompleteDataResult(CbcImportData cbcImportData, CbcImportResultType resultType) {
        // The user should be using CbcImportMissingDataResult instead if this is null...
        Validate.notNull(cbcImportData);
        this.cbcImportData = cbcImportData;
        this.resultType = resultType;
    }
    
    public CbcImportData getCbcImportData() {
        return cbcImportData;
    }
    
    public CbcImportResultType getResultType() {
        return resultType;
    }
    
    @Override
    public Element getResponseElement(Namespace ns) {
        Element response = new Element("cbcImportResponse", ns);
        
        if (!resultType.isSuccess()) {
            Element cbcResult = new Element("cbcImportResultType", ns);
            cbcResult.addContent(resultType.getDbString());
            response.addContent(cbcResult);
        }
        
        ImportAction action = cbcImportData.getImportAction();
        String name = cbcImportData.getCbcName();
        String outcome = resultType.isSuccess() ? "succeeded" : "failed";
        String actionString = (action != null) ? action.getDbString() : "Import"; 
        
        String detail = actionString + " " + outcome + " for CBC with name \"" + name + "\".";
        
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
        YukonMessageSourceResolvable message = null;
        
        String key = "yukon.web.modules.capcontrol.import.cbc";
        
        key += getResultKey(cbcImportData.getImportAction(), resultType.isSuccess());
        
        message = new YukonMessageSourceResolvable(key, resultType.getDbString(), 
                                                   cbcImportData.getCbcName());
        
        return message;
    }
}
