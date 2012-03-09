package com.cannontech.capcontrol.creation.model;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class HierarchyImportInvalidDataResult implements HierarchyImportResult {

    private final CapControlImporterHierarchyField invalidField;
    private final HierarchyImportResultType resultType;
    
    public HierarchyImportInvalidDataResult(CapControlImporterHierarchyField invalidField, 
                                            HierarchyImportResultType resultType) {
        this.invalidField = invalidField;
        this.resultType = resultType;
    }
    
    @Override
    public Element getResponseElement(Namespace ns) {
        Element response = new Element("hierarchyImportResponse", ns);
        
        Element resultTypeElement = new Element("hierarchyImportResultType", ns);
        resultTypeElement.addContent(resultType.getDbString());
        response.addContent(resultTypeElement);

        Element detailElement = new Element("resultDetail", ns);
        String detail = "Hierarchy Import failed because it has invalid data in field " + invalidField.getColumnName();
        detailElement.addContent(detail);

        response.addContent(detailElement);
        
        return response;
    }

    @Override
    public YukonMessageSourceResolvable getMessage() {
        String key = "yukon.web.modules.capcontrol.import.invalidDataResult";
        
        return new YukonMessageSourceResolvable(key, resultType.getDbString(), invalidField.getColumnName());
    }

    @Override
    public HierarchyImportResultType getResultType() {
        return resultType;
    }
}
