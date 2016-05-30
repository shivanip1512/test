package com.cannontech.capcontrol.creation.model;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class HierarchyImportInvalidDataResult implements HierarchyImportResult {

    private final CapControlImporterHierarchyField invalidField;
    private final HierarchyImportResultType resultType;
    
    public HierarchyImportInvalidDataResult(HierarchyImportResultType resultType) {
        this.resultType = resultType;
        this.invalidField = getFieldForType(resultType);
    }
    
    private CapControlImporterHierarchyField getFieldForType(HierarchyImportResultType resultType) {
        switch(resultType) {
        case INVALID_DISABLED_VALUE:
            return CapControlImporterHierarchyField.DISABLED;
        case INVALID_IMPORT_ACTION:
            return CapControlImporterHierarchyField.IMPORT_ACTION;
        case INVALID_OPERATIONAL_STATE:
            return CapControlImporterHierarchyField.OPERATIONAL_STATE;
        case INVALID_PARENT:
            return CapControlImporterHierarchyField.PARENT;
        case INVALID_TYPE:
            return CapControlImporterHierarchyField.TYPE;
        case ILLEGAL_CHARS:
            return CapControlImporterHierarchyField.NAME;
        default:
            return null;
        }
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
